package com.kanniappan.urlshortener.service;

import com.kanniappan.urlshortener.cache.CacheKey;
import com.kanniappan.urlshortener.cache.CachedUrl;
import com.kanniappan.urlshortener.cache.UrlValidationData;
import com.kanniappan.urlshortener.dto.*;
import com.kanniappan.urlshortener.entity.Url;
import com.kanniappan.urlshortener.entity.User;
import com.kanniappan.urlshortener.exception.UrlExpiredException;
import com.kanniappan.urlshortener.exception.UrlInactiveException;
import com.kanniappan.urlshortener.exception.UrlNotFoundException;
import com.kanniappan.urlshortener.repository.UrlRepository;
import com.kanniappan.urlshortener.util.EmailUtils;
import com.kanniappan.urlshortener.util.ShortCodeGenerator;
import com.kanniappan.urlshortener.util.UrlUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UrlService {

    private final UrlRepository urlRepository;
    private final RedisService redisService;


    private User getCurrentUser() {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        return (User) authentication.getPrincipal();

    }


    public List<CreateUrlResponse> getAllUrls() {
        List<Url> urls = urlRepository.findAll();

        List<CreateUrlResponse> responses = new ArrayList<>();

        for (Url url : urls) {
            responses.add(mapToResponse(url));
        }

        return responses;
    }

    @Transactional
    public CreateUrlResponse createShortUrl(CreateUrlRequest request) {

        User user = getCurrentUser();

        String email = UrlUtil.normalize(request.getOriginalUrl());

        Optional<Url> existingUrl = urlRepository.findByUserAndOriginalUrl(user, email);

        if (existingUrl.isPresent()) {

            return mapToResponse(existingUrl.get());

        }

        String shortCode = generateUniqueShortCode();

        Url url = buildUrlEntity(request, shortCode, user);

        Url savedUrl = urlRepository.save(url);

        return mapToResponse(savedUrl);
    }

    private String generateUniqueShortCode() {
        String shortCode;
        do {
            shortCode = ShortCodeGenerator.generateShortCode();
        } while (urlRepository.findByShortCode(shortCode).isPresent());

        return shortCode;
    }

    private Url buildUrlEntity(CreateUrlRequest request, String shortCode, User user) {
        LocalDateTime now = LocalDateTime.now();
        return Url.builder().originalUrl(UrlUtil.normalize(request.getOriginalUrl())).shortCode(shortCode).expiresAt(now.plusYears(1)).user(user).build();
    }

    private CreateUrlResponse mapToResponse(Url savedUrl) {

        return CreateUrlResponse.builder().id(savedUrl.getId()).originalUrl(savedUrl.getOriginalUrl()).clickCount(savedUrl.getClickCount()).shortCode(savedUrl.getShortCode()).shortUrl("http://localhost:8080/" + savedUrl.getShortCode()).build();
    }

    @Transactional
    public String getOriginalUrl(String shortCode) {

        LocalDateTime now = LocalDateTime.now();

        CacheKey cacheKey = CacheKey.url(shortCode);

        CachedUrl cachedUrl = redisService.get(cacheKey, CachedUrl.class);

        if (cachedUrl != null) {

            validateUrl(cachedUrl, shortCode, now);

            updateStatistics(shortCode, now);

            return cachedUrl.originalUrl();

        }
        Url url = loadUrl(shortCode);

        validateUrl(url, shortCode, now);

        redisService.save(cacheKey, CachedUrl.from(url), Duration.ofMinutes(5));

        updateStatistics(shortCode, now);

        return url.getOriginalUrl();
    }

    private void validateUrl(UrlValidationData url, String shortCode, LocalDateTime now) {
        if (!url.active()) {
            throw new UrlInactiveException("Short code '" + shortCode + "' is inactive");
        }
        if (url.expiresAt() != null && url.expiresAt().isBefore(now)) {
            throw new UrlExpiredException("Short code '" + shortCode + "' has expired");
        }
    }

    private void updateStatistics(String shortCode, LocalDateTime now) {
//        url.setClickCount(url.getClickCount() + 1);
        urlRepository.incrementClickCount(shortCode, now);
//        urlRepository.save(url); //Because this is called under transactional method
    }

    private UrlResponse mapToUrlResponse(Url url) {

        return new UrlResponse(url.getOriginalUrl(), url.getShortCode(), "http://localhost:8080/" + url.getShortCode(), url.getClickCount(), url.getCreatedAt(), url.getExpiresAt(), url.getIsActive());
    }

    public PagedResponse<UrlResponse> getMyUrls(int page, int size) {

        User currentUser = getCurrentUser();

        Pageable pageable = PageRequest.of(page, size);

        Page<Url> urls = urlRepository.findByUserAndIsActiveTrue(currentUser, pageable);

        Page<UrlResponse> responsePage = urls.map(this::mapToUrlResponse);

        return PagedResponse.from(responsePage);

    }

    @Transactional
    public void deleteUrl(String shortCode) {

        User currentUser = getCurrentUser();

        Optional<Url> existingUrl = urlRepository.findByShortCodeAndUser(shortCode, currentUser);

        if (existingUrl.isEmpty()) {
            throw new UrlNotFoundException("Url Not Found");
        }

        Url url = existingUrl.get();

        url.setIsActive(false);

        invalidateCache(shortCode);
        invalidateCache(shortCode);

    }

    private Url loadUrl(String shortCode) {
        System.out.println("DATABASE HIT");
        return urlRepository.findByShortCode(shortCode).orElseThrow(() -> new UrlNotFoundException("Short code '" + shortCode + "' not found"));
    }

    @Transactional
    public void updateUrl(String shortCode, UpdateRequest request) {
        User currentUser = getCurrentUser();

        Optional<Url> existingUrl = urlRepository.findByShortCodeAndUser(shortCode, currentUser);

        if (existingUrl.isEmpty()) {
            throw new UrlNotFoundException("Url Not Found");
        }

        Url url = existingUrl.get();

        url.setOriginalUrl(UrlUtil.normalize(request.originalUrl()));

        if (request.expiresAt() != null) {
            url.setExpiresAt(request.expiresAt());
        }

        invalidateCache(shortCode);
    }

    public void invalidateCache(String shortCode) {

        CacheKey cacheKey = CacheKey.url(shortCode);

        redisService.delete(cacheKey);
    }

}
