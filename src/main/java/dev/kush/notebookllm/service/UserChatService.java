package dev.kush.notebookllm.service;

import dev.kush.notebookllm.controller.UserController;

import java.util.List;

public interface UserChatService {
    List<UserController.UserChatDto> getUserChatByUserId(long userId);
}
