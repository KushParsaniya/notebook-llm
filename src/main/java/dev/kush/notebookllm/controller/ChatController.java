package dev.kush.notebookllm.controller;

import dev.kush.notebookllm.advisor.CaptureMemoryAdvisor;
import dev.kush.notebookllm.service.ChatHistoryService;
import dev.kush.notebookllm.service.impl.CustomCassandraChatMemory;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.StringUtils;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.messages.MessageType;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.springframework.ai.chat.client.advisor.AbstractChatMemoryAdvisor.CHAT_MEMORY_CONVERSATION_ID_KEY;

@RestController
@RequestMapping("/api/v1/chat")
public class ChatController {

    private final ChatHistoryService chatHistoryService;

    public record ChatMessageRequest(String chatId, String message) {
    }

    public record ChatMessageResponse(String content, String chatId) {
    }

    public record ChatHistoryResponse(String message, MessageType messageType, Instant createdAt) {
    }

    private final ChatClient chatClient;
    private final VectorStore vectorStore;

    public ChatController(@Qualifier("openAiChatModel") ChatModel chatModel,
                          CustomCassandraChatMemory customCassandraChatMemory,
                          @Value("classpath:prompts/use-capture-memory.sh") Resource useCaptureMemoryPrompt,
                          CaptureMemoryAdvisor captureMemoryAdvisor, VectorStore vectorStore,
                          ChatHistoryService chatHistoryService) {
        this.vectorStore = vectorStore;
        this.chatClient = ChatClient.builder(chatModel)
                .defaultSystem(useCaptureMemoryPrompt)
                .defaultAdvisors(
                        new MessageChatMemoryAdvisor(customCassandraChatMemory),
                        captureMemoryAdvisor
                )
                .build();
        this.chatHistoryService = chatHistoryService;
    }


    @PostMapping("")
    public ChatMessageResponse chat(@RequestBody ChatMessageRequest chatMessageRequest, HttpServletResponse response, Principal principal) {
        var result = vectorStore.similaritySearch("username == %s".formatted(principal.getName()));
        String chatId = chatMessageRequest.chatId();
        if (StringUtils.isBlank(chatId)) {
            chatId = UUID.randomUUID().toString();
        }
        final String finalChatId = chatId;
        var content = chatClient
                .prompt()
                .advisors(advisorSpec -> advisorSpec.param(CHAT_MEMORY_CONVERSATION_ID_KEY, finalChatId))
                .system(
                        promptSystemSpec -> promptSystemSpec.param("memory",
                                result.stream()
                                        .map(Document::getContent)
                                        .collect(Collectors.joining(","))))
                .user(chatMessageRequest.message())
                .call().content();
        return new ChatMessageResponse(content, finalChatId);
    }

    @GetMapping("/chat-history")
    public ResponseEntity<List<ChatHistoryResponse>> getChatHistory(@RequestParam String chatId) {
        return ResponseEntity.ok(chatHistoryService.findChatHistoryByChatIdAndUsername(chatId));
    }

}
