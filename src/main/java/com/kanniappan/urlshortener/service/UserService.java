package com.kanniappan.urlshortener.service;

import com.kanniappan.urlshortener.dto.CreateUserRequest;
import com.kanniappan.urlshortener.dto.UserResponse;
import com.kanniappan.urlshortener.entity.User;
import com.kanniappan.urlshortener.exception.UserAlreadyExistsException;
import com.kanniappan.urlshortener.repository.UserRepository;
import com.kanniappan.urlshortener.util.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;


    @Transactional
    public UserResponse register(CreateUserRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new UserAlreadyExistsException("User with email " + request.getEmail() + " already exist");
        }

        User user = User.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Role.USER)
                .build();

        User savedUser = userRepository.save(user);

        return mapToResponse(savedUser);
    }

    private UserResponse mapToResponse(User savedUser) {
        return new UserResponse(savedUser.getId(), savedUser.getEmail(), savedUser.getRole());
    }
}
