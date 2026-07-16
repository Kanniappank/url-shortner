package com.kanniappan.urlshortener.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CreateUrlRequest {
    @NotBlank(message = "Original Url is required")
    private String originalUrl;
}
