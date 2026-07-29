package com.kanniappan.urlshortener.cache;

import com.kanniappan.urlshortener.entity.Url;

import java.time.LocalDateTime;

public record CachedUrl(String originalUrl, boolean active, LocalDateTime expiresAt) implements UrlValidationData{
    public static CachedUrl from(Url url) {
        return new CachedUrl(url.getOriginalUrl(), url.getIsActive(), url.getExpiresAt());
    }
}
