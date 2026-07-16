package com.kanniappan.urlshortener.service;

import com.kanniappan.urlshortener.dto.CreateUrlRequest;
import com.kanniappan.urlshortener.dto.UrlResponse;
import com.kanniappan.urlshortener.entity.Url;
import com.kanniappan.urlshortener.repository.UrlRepository;
import com.kanniappan.urlshortener.util.ShortCodeGenerator;

import java.time.LocalDateTime;

public class UrlService {

    private final UrlRepository urlRepository;

    public UrlService(UrlRepository urlRepository) {
        this.urlRepository = urlRepository;
    }

    public UrlResponse createShortUrl(CreateUrlRequest request) {

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

    private Url buildUrlEntity(CreateUrlRequest request,String shortCode){
        LocalDateTime now = LocalDateTime.now();
        return Url.builder()
                .originalUrl(request.getOriginalUrl())
                .createdAt(now)
                .updatedAt(now)
                .shortCode(shortCode)
                .expiresAt(now.plusYears(1))
                .build();
    }

    private UrlResponse mapToResponse(Url savedUrl){

        return UrlResponse.builder()
                .id(savedUrl.getId())
                .originalUrl(savedUrl.getOriginalUrl())
                .shortUrl(savedUrl.getShortCode())
                .clickCount(savedUrl.getClickCount())
                .shortUrl("http://localhost:8080/"+savedUrl.getShortCode())
                .build();
    }

}
