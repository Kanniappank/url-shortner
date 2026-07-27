package com.kanniappan.urlshortener.controller;

import com.kanniappan.urlshortener.dto.LoginRequest;
import com.kanniappan.urlshortener.dto.UserRequest;
import com.kanniappan.urlshortener.dto.LoginResponse;
import com.kanniappan.urlshortener.dto.UserResponse;
import com.kanniappan.urlshortener.service.AuthService;
import com.kanniappan.urlshortener.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final UserService userService;
    private final AuthService authService;

    public AuthController(UserService userService, AuthService authService) {
        this.userService = userService;
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<UserResponse> register(@Valid @RequestBody UserRequest request) {
        UserResponse response = userService.register(request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        System.out.println(">>> Login controller reached");
        LoginResponse response = authService.login(request);
        return ResponseEntity.status(HttpStatus.OK)
                .body(response);
    }
}
