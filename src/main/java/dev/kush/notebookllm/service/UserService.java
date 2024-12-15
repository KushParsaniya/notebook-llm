package dev.kush.notebookllm.service;

import dev.kush.notebookllm.controller.UserController;

public interface UserService {
    String getCurrentUsername();

    long getCurrentUserId();

    UserController.CreateUserResponse createUser(String username, String password, String phoneNumber);

    String loginUser(String username, String password);

    boolean updateDeletedByUsername(boolean deleted);

    boolean updatePasswordByUsername(String password);
}
