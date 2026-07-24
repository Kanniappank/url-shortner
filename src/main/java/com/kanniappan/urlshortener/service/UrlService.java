package com.kanniappan.urlshortener.service;

import com.kanniappan.urlshortener.dto.CreateUrlRequest;
import com.kanniappan.urlshortener.dto.CreateUrlResponse;
import com.kanniappan.urlshortener.entity.Url;
import com.kanniappan.urlshortener.exception.UrlExpiredException;
import com.kanniappan.urlshortener.exception.UrlInactiveException;
import com.kanniappan.urlshortener.exception.UrlNotFoundException;
import com.kanniappan.urlshortener.repository.UrlRepository;
import com.kanniappan.urlshortener.util.ShortCodeGenerator;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class UrlService {

    private final UrlRepository urlRepository;

    public UrlService(UrlRepository urlRepository) {
        this.urlRepository = urlRepository;
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

        String shortCode = generateUniqueShortCode();

        Url url = buildUrlEntity(request, shortCode);

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

    private Url buildUrlEntity(CreateUrlRequest request, String shortCode) {
        LocalDateTime now = LocalDateTime.now();
        return Url.builder()
                .originalUrl(request.getOriginalUrl())
                .shortCode(shortCode)
                .expiresAt(now.plusYears(1))
                .build();
    }

    private CreateUrlResponse mapToResponse(Url savedUrl) {

        return CreateUrlResponse.builder()
                .id(savedUrl.getId())
                .originalUrl(savedUrl.getOriginalUrl())
                .clickCount(savedUrl.getClickCount())
                .shortCode(savedUrl.getShortCode())
                .shortUrl("http://localhost:8080/" + savedUrl.getShortCode())
                .build();
    }


    @Transactional
    public String getOriginalUrl(String shortCode) {
        Url url = urlRepository.findByShortCode(shortCode).orElseThrow(() -> new UrlNotFoundException("Short code '" + shortCode + "' not found"));

        LocalDateTime now = LocalDateTime.now();

        validateUrl(url, now);

        updateStatistics(url, now);

        return url.getOriginalUrl();
    }

    private void validateUrl(Url url, LocalDateTime now) {
        if (Boolean.FALSE.equals(url.getIsActive())) {
            throw new UrlInactiveException("Short code '" + url.getShortCode() + "' is inactive");
        }
        if (url.getExpiresAt() != null && url.getExpiresAt().isBefore(now)) {
            throw new UrlExpiredException("Short code '" + url.getShortCode() + "' has expired");
        }
    }

    private void updateStatistics(Url url, LocalDateTime now) {
        url.setClickCount(url.getClickCount() + 1);
//        urlRepository.save(url); //Because this is called under transactional method
    }


}
