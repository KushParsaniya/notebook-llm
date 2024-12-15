package dev.kush.notebookllm.service.impl;

import dev.kush.notebookllm.entity.ChatHistory;
import dev.kush.notebookllm.entity.UserChat;
import dev.kush.notebookllm.service.ChatHistoryService;
import dev.kush.notebookllm.service.UserChatService;
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

    private final ChatHistoryService chatHistoryService;
    private final UserService userService;
    private final UserChatService userChatService;

    @Override
    public void add(String conversationId, List<Message> messages) {
        // TODO: username from security context
        var userId = userService.getCurrentUserId();
        var username = userService.getCurrentUsername();
        if (!userChatService.existsByUsernameAndChatId(userId, conversationId)) {
            userChatService.save(new UserChat(userId, username, getTitle(messages), conversationId));
        }
        log.info("Adding messages to conversationId: {}", conversationId);
        var chatHistories = messages.stream()
                .map(message -> getChatHistory(conversationId, message, userId, username))
                .toList();
        chatHistoryService.saveAll(chatHistories);
    }

    private static String getTitle(List<Message> messages) {
        return messages.getFirst().getContent().substring(0, 20);
    }

    private ChatHistory getChatHistory(String conversationId, Message message, long userId, String username) {
        return new ChatHistory(conversationId, userId, username,
                message.getContent(), message.getMessageType(), Instant.now());
    }

    @Override
    public List<Message> get(String conversationId, int lastN) {
        log.info("Getting chat history for conversationId: {}", conversationId);
        return chatHistoryService.findByChatId(conversationId, lastN).stream()
                .map(this::createMessage)
                .toList();
    }

    private Message createMessage(ChatHistory chatHistory) {
        return switch (chatHistory.getMessageType()) {
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
