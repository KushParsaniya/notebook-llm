package dev.kush.notebookllm.service.impl;

import dev.kush.notebookllm.constant.ExceptionMessages;
import dev.kush.notebookllm.repository.UserRepository;
import dev.kush.notebookllm.service.SecuredUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SecuredUserServiceImpl implements SecuredUserService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByUsername(username)
                .map(dev.kush.notebookllm.dto.SecuredUser::new)
                .orElseThrow(() -> new UsernameNotFoundException(ExceptionMessages.USER_NOT_FOUND));
    }
}
