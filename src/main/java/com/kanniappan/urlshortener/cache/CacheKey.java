package com.kanniappan.urlshortener.cache;

public record CacheKey(String value) {
    public static CacheKey url(String shortCode) {
        return new CacheKey("url:" + shortCode);
    }

    public static CacheKey user(Long userId) {
        return new CacheKey("user:" + userId);
    }

    public static CacheKey rateLimit(String ip) {
        return new CacheKey("rate:" + ip);
    }

    @Override
    public String toString() {
        return value;
    }
}
