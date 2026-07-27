package com.kanniappan.urlshortener.dto;


import com.kanniappan.urlshortener.constants.Role;

public record UserResponse(
        Long id,
        String email,
        Role role
) {
}
