package dev.kush.notebookllm.advisor;

import dev.kush.notebookllm.service.impl.CustomCassandraChatMemory;
import dev.kush.notebookllm.service.impl.MemoryBasicExtractor;
import dev.kush.notebookllm.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.api.AdvisedRequest;
import org.springframework.ai.chat.client.advisor.api.AdvisedResponse;
import org.springframework.ai.chat.client.advisor.api.CallAroundAdvisor;
import org.springframework.ai.chat.client.advisor.api.CallAroundAdvisorChain;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.document.Document;
import org.springframework.ai.model.Content;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.retry.RetryCallback;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

import static org.springframework.ai.chat.client.advisor.AbstractChatMemoryAdvisor.CHAT_MEMORY_CONVERSATION_ID_KEY;

@Component
@Slf4j
public class CaptureMemoryAdvisor implements CallAroundAdvisor {

    public record MemoryLLMResponse(String content, boolean isUseful) {
    }

    private final ChatClient chatClient;
    private final VectorStore vectorStore;
    private final ExecutorService executorService;
    private final RetryTemplate retryTemplate;
    private final MemoryBasicExtractor memoryBasicExtractor;
    private final CustomCassandraChatMemory customCassandraChatMemory;
    private final UserService userService;

    public CaptureMemoryAdvisor(@Value("classpath:prompts/capture-memory.sh") Resource captureMemoryPrompt,
                                @Qualifier("ollamaChatModel") ChatModel chatModel,
                                VectorStore vectorStore, RetryTemplate retryTemplate,
                                MemoryBasicExtractor memoryBasicExtractor, CustomCassandraChatMemory customCassandraChatMemory, UserService userService) {
        this.customCassandraChatMemory = customCassandraChatMemory;
        this.userService = userService;
        this.chatClient = ChatClient
                .builder(chatModel)
                .defaultSystem(captureMemoryPrompt)
                .build();
        this.vectorStore = vectorStore;
        this.memoryBasicExtractor = memoryBasicExtractor;
        this.executorService = Executors.newVirtualThreadPerTaskExecutor();
        this.retryTemplate = retryTemplate;
    }

    @Override
    public AdvisedResponse aroundCall(AdvisedRequest advisedRequest, CallAroundAdvisorChain chain) {
        var response = chain.nextAroundCall(advisedRequest);
        executorService.submit(backgroundTask(advisedRequest, response));
        return response;
    }

    private Runnable backgroundTask(AdvisedRequest advisedRequest, AdvisedResponse response) {
        return () -> {
            try {
                retryTemplate.execute((RetryCallback<Boolean, Throwable>) retryContext -> captureMemoryTask(advisedRequest, response));
            } catch (Throwable e) {
                e.printStackTrace();
            }
        };
    }

    private boolean captureMemoryTask(AdvisedRequest advisedRequest, AdvisedResponse response) {
        // TODO: extract useful info form response.
        log.info("starting captureMemoryTask --->");
        var userContent = memoryBasicExtractor.extractRequestContent(advisedRequest);
        var assistantContent = memoryBasicExtractor.extractResponseContent(response);
        String chatId = (String) advisedRequest.adviseContext().getOrDefault(CHAT_MEMORY_CONVERSATION_ID_KEY, "");
        var capturedMemory = chatClient.prompt()
                .system(promptSystemSpec -> promptSystemSpec.param(
                        "memory", customCassandraChatMemory.get(chatId, 10).stream()
                                .map(Content::getContent)
                                .collect(Collectors.joining(","))))
//                .messages(new AssistantMessage(assistantContent))
                .user(userContent)
                .call()
                .entity(MemoryLLMResponse.class);

        boolean isUseful = capturedMemory != null && capturedMemory.isUseful();
        if (isUseful) {
            // TODO: username from SecurityContext
            String username = userService.getCurrentUsername();
            log.info("Captured memory: {}, chatId: {}", capturedMemory.content(), chatId);
            vectorStore.accept(
                    List.of(new Document("""
                            Remember this about user:
                            %s
                            """.formatted(capturedMemory.content()),
                            Map.of(
                                    "chatId", chatId,
                                    "username", username
                            ))));
        }
        return isUseful;
    }


    @Override
    public String getName() {
        return "capture-memory";
    }

    @Override
    public int getOrder() {
        return 0;
    }
}
