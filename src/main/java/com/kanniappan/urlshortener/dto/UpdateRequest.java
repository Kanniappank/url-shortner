package com.kanniappan.urlshortener.dto;

import jakarta.validation.Valid;
import org.hibernate.validator.constraints.URL;

import java.time.LocalDateTime;

public record UpdateRequest(@Valid @URL String originalUrl, LocalDateTime expiresAt) {
}
