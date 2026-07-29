package com.kanniappan.urlshortener.dto;

import java.time.Instant;
import java.time.LocalDateTime;

public record UrlResponse(
        String originalUrl,
        String shortCode,
        String shortUrl,
        Long clickCount,
        LocalDateTime createdAt,
        LocalDateTime expiresAt,
        boolean isActive
) {
}
