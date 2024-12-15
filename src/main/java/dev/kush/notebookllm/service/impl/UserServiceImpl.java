package dev.kush.notebookllm.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.kush.notebookllm.constant.ProjectConstant;
import dev.kush.notebookllm.controller.UserController;
import dev.kush.notebookllm.entity.User;
import dev.kush.notebookllm.enums.UserRole;
import dev.kush.notebookllm.exception.InvalidCredentialsException;
import dev.kush.notebookllm.repository.UserRepository;
import dev.kush.notebookllm.service.UserService;
import jakarta.persistence.EntityExistsException;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final ObjectMapper objectMapper;
    private final JwtEncoder jwtEncoder;
    private final AuthenticationManager authenticationManager;

    @Override
    public String getCurrentUsername() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }

    @Override
    public long getCurrentUserId() {
        String currentUsername = getCurrentUsername();
        if (StringUtils.isBlank(currentUsername)) return 0;
        return userRepository.findUserIdByUsername(currentUsername);
    }

    @Override
    public UserController.CreateUserResponse createUser(String username, String password, String phoneNumber) {
        if (userRepository.existsByUsername(username)) {
            throw new EntityExistsException("User already exists");
        }

        if (StringUtils.isBlank(username) || StringUtils.isBlank(password)) {
            throw new IllegalArgumentException("Username and password cannot be empty");
        }
        try {
            var user = userRepository.save(new User(username, passwordEncoder.encode(password), phoneNumber, UserRole.USER));
            return objectMapper.convertValue(user, UserController.CreateUserResponse.class);
        } catch (Exception e) {
            throw new RuntimeException("Error creating user");
        }
    }

    @Override
    public String loginUser(String username, String password) {
        if (StringUtils.isBlank(username) || StringUtils.isBlank(password)) {
            throw new IllegalArgumentException("Username and password cannot be empty");
        }
        try {
            Authentication authentication = new UsernamePasswordAuthenticationToken(username, password);
            var authUser = authenticationManager.authenticate(authentication);
            var securityContext = SecurityContextHolder.createEmptyContext();
            securityContext.setAuthentication(authUser);
            SecurityContextHolder.setContext(securityContext);
            return getJwtToken(username, authUser);
        } catch (AuthenticationException e) {
            throw new InvalidCredentialsException("Invalid username or password");
        }
    }

    @Override
    @Transactional
    public boolean updateDeletedByUsername(boolean deleted) {
        return userRepository.updateDeletedByUsername(getCurrentUsername(), true) > 0;
    }

    @Override
    @Transactional
    public boolean updatePasswordByUsername(String password) {
        return userRepository.updatePasswordByUsername(getCurrentUsername(), passwordEncoder.encode(password)) > 0;
    }

    private String getJwtToken(String username, Authentication authUser) {
        return jwtEncoder.encode(
                JwtEncoderParameters.from(JwtClaimsSet.builder()
                        .subject(username)
                        .issuer(ProjectConstant.ISSUER)
                        .issuedAt(Instant.now())
                        .expiresAt(Instant.now().plusSeconds(60 * 60 * 24))
                        .claim(ProjectConstant.AUTHORITIES_CLAIM,
                                authUser.getAuthorities().stream()
                                        .map(GrantedAuthority::getAuthority)
                                        .collect(Collectors.joining(",")))
                        .build())
        ).getTokenValue();
    }
}
