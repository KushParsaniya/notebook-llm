package dev.kush.notebookllm.service;

import dev.kush.notebookllm.controller.UserController;
import dev.kush.notebookllm.entity.UserChat;

import java.util.List;

public interface UserChatService {
    List<UserController.UserChatDto> getUserChatByUserId(long userId);

    boolean existsByUsernameAndChatId(long userId, String chatId);

    UserChat save(UserChat userChat);
}
