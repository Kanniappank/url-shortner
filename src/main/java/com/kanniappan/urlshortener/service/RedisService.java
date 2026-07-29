package com.kanniappan.urlshortener.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kanniappan.urlshortener.cache.CacheKey;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class RedisService {

    private final RedisTemplate<String, String> redisTemplate;
    private final ObjectMapper objectMapper;

    public <T> void save(CacheKey key, T value, Duration ttl) {

        try {
            String json = objectMapper.writeValueAsString(value);

            redisTemplate.opsForValue().set(
                    key.toString(),
                    json,
                    ttl
            );

        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to serialize object", e);
        }
    }

    public <T> T get(CacheKey key, Class<T> clazz) {

        String json = redisTemplate.opsForValue().get(key.toString());

        if (json == null) {
            return null;
        }

        try {
            return objectMapper.readValue(json, clazz);

        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to deserialize object", e);
        }
    }

    public void delete(CacheKey key) {
        redisTemplate.delete(key.toString());
    }
}