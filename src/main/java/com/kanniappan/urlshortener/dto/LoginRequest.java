package com.kanniappan.urlshortener.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record LoginRequest(
        @NotBlank(message = "Email is required")
        @Email(message = "Enter a valid email")
        String email,
        @NotBlank(message = "Password is required")
        String password) {
}
