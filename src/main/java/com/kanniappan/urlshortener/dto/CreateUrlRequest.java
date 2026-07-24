package com.kanniappan.urlshortener.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.hibernate.validator.constraints.URL;

@Data
public class CreateUrlRequest {
    @NotBlank(message = "Original Url is required")
    @URL(message = "Enter a valid URL")
    private String originalUrl;
}
