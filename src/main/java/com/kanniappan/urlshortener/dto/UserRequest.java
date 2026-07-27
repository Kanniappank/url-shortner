package com.kanniappan.urlshortener.dto;

import com.kanniappan.urlshortener.constants.ValidationConstants;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class UserRequest {
    @Email(message = "Provide a valid email")
    @NotBlank(message = "Email is required")
    private String email;

    @NotBlank(message = "Password is required")
    @Pattern(
            regexp = ValidationConstants.PASSWORD_REGEX,
            message = "Password must contain at least 8 characters, one uppercase letter, one lowercase letter, one digit and one special character"
    )
    private String password;
}
