package dev.kush.notebookllm.service;

import dev.kush.notebookllm.controller.ChatController;
import dev.kush.notebookllm.entity.ChatHistory;

import java.util.List;

public interface ChatHistoryService {

    List<ChatController.ChatHistoryResponse> findChatHistoryByChatIdAndUsername(String chatId);

    List<ChatHistory> findByChatId(String conversationId, int lastN);

    void saveAll(List<ChatHistory> chatHistories);
}
