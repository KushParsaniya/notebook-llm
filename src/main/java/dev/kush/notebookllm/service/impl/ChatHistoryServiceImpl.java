package dev.kush.notebookllm.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.kush.notebookllm.controller.ChatController;
import dev.kush.notebookllm.entity.ChatHistory;
import dev.kush.notebookllm.repository.ChatHistoryRepository;
import dev.kush.notebookllm.service.ChatHistoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ChatHistoryServiceImpl implements ChatHistoryService {

    private final ChatHistoryRepository chatHistoryRepository;
    private final ObjectMapper objectMapper;

    @Override
    public List<ChatController.ChatHistoryResponse> findChatHistoryByChatIdAndUsername(String chatId) {
        return chatHistoryRepository.findChatHistoryByChatIdAndUsername(chatId)
                .stream().map(chatHistory -> objectMapper.convertValue(chatHistory, ChatController.ChatHistoryResponse.class))
                .toList();
    }

    @Override
    public List<ChatHistory> findByChatId(String conversationId, int lastN) {
        return chatHistoryRepository.findByChatId(conversationId, lastN);
    }

    @Override
    public void saveAll(List<ChatHistory> chatHistories) {
        chatHistoryRepository.saveAll(chatHistories);
    }
}
