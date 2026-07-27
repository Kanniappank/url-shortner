package com.kanniappan.urlshortener.service;

import com.kanniappan.urlshortener.dto.UserRequest;
import com.kanniappan.urlshortener.dto.UserResponse;
import com.kanniappan.urlshortener.entity.User;
import com.kanniappan.urlshortener.exception.UserAlreadyExistsException;
import com.kanniappan.urlshortener.repository.UserRepository;
import com.kanniappan.urlshortener.util.EmailUtils;
import com.kanniappan.urlshortener.constants.Role;
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
    public UserResponse register(UserRequest request) {
        String email = EmailUtils.normalize(request.getEmail());
        if (userRepository.existsByEmail(email)) {
            throw new UserAlreadyExistsException("User with email " + email + " already exist");
        }

        User user = User.builder()
                .email(email)
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
