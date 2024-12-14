package dev.kush.notebookllm.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.kush.notebookllm.controller.UserController;
import dev.kush.notebookllm.repository.UserChatRepository;
import dev.kush.notebookllm.service.UserChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserChatServiceImpl implements UserChatService {

    private final UserChatRepository userChatRepository;
    private final ObjectMapper objectMapper;

    @Override
    public List<UserController.UserChatDto> getUserChatByUserId(long userId) {
        if (userId == 0) {
            return List.of();
        }
        var userChats = userChatRepository.getUserChatsByUserId(userId);
        return userChats.stream()
                .map(userChat -> objectMapper.convertValue(userChat, UserController.UserChatDto.class))
                .toList();
    }

}
