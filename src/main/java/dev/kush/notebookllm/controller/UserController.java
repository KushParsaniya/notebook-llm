package dev.kush.notebookllm.controller;

import dev.kush.notebookllm.constant.ProjectConstant;
import dev.kush.notebookllm.service.UserChatService;
import dev.kush.notebookllm.service.UserService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.validator.constraints.Length;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/user")
@RequiredArgsConstructor
public class UserController {

    public record CreateUserRequest(@Email(message = ProjectConstant.EMAIL_VALIDATION) String username,
                                    @Length(min = 8, message = ProjectConstant.PASSWORD_VALIDATION) String password,
                                    String phoneNumber) {
    }

    public record CreateUserResponse(long userId, String username) {
    }

    public record LoginUserRequest(@Email(message = ProjectConstant.EMAIL_VALIDATION) String username,
                                   @Length(min = 8, message = ProjectConstant.PASSWORD_VALIDATION) String password) {
    }

    // TODO: return user's pdfs list
    public record LoginUserResponse(String token, List<UserChatDto> userChatDtos) {
    }

    public record UserChatDto(String chatId, String title) {
    }

    private final UserService userService;
    private final UserChatService userChatService;


    @PostMapping("/create")
    public ResponseEntity<CreateUserResponse> createUser(@Valid @RequestBody CreateUserRequest request) {
        var createdUser = userService.createUser(request.username(), request.password(), request.phoneNumber());
        return ResponseEntity.ok(new CreateUserResponse(createdUser.userId(), createdUser.username()));
    }

    @PostMapping("/login")
    public ResponseEntity<LoginUserResponse> login(@Valid @RequestBody LoginUserRequest request) {
        final var username = request.username();
        var token = userService.loginUser(username, request.password());
        if (StringUtils.isBlank(token)) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        return ResponseEntity.ok(new LoginUserResponse(token, userChatService.getUserChatByUserId(userService.getCurrentUserId())));
    }
}