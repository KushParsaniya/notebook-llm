package dev.kush.notebookllm.service.impl;

import dev.kush.notebookllm.entity.ChatHistory;
import dev.kush.notebookllm.entity.UserChat;
import dev.kush.notebookllm.repository.ChatHistoryRepository;
import dev.kush.notebookllm.repository.UserChatRepository;
import dev.kush.notebookllm.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.MessageType;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class CustomCassandraChatMemory implements ChatMemory {

    private final UserChatRepository userChatRepository;
    private final ChatHistoryRepository chatHistoryRepository;
    private final UserService userService;

    @Override
    public void add(String conversationId, List<Message> messages) {
        // TODO: username from security context
        UserChat userChat = new UserChat();
        log.info("Adding messages to conversationId: {}", conversationId);
        var chatHistories = messages.stream()
                .map(message -> getChatHistory(conversationId, message))
                .toList();
        chatHistoryRepository.saveAll(chatHistories);
    }

    private ChatHistory getChatHistory(String conversationId, Message message) {
        return new ChatHistory(conversationId, userService.getCurrentUserId(), userService.getCurrentUsername(),
                message.getContent(), message.getMessageType().getValue(), Instant.now());
    }

    @Override
    public List<Message> get(String conversationId, int lastN) {
        log.info("Getting chat history for conversationId: {}", conversationId);
        return chatHistoryRepository.findByChatId(conversationId, lastN).stream()
                .map(this::createMessage)
                .toList();
    }

    private Message createMessage(ChatHistory chatHistory) {
        return switch (MessageType.valueOf(chatHistory.getMessageType())) {
            case MessageType.USER, TOOL -> new UserMessage(chatHistory.getMessage());
            case ASSISTANT -> new AssistantMessage(chatHistory.getMessage());
            case SYSTEM -> new SystemMessage(chatHistory.getMessage());
        };
    }

    @Override
    public void clear(String conversationId) {
        log.info("Clearing chat history for conversationId: {}", conversationId);
    }
}
