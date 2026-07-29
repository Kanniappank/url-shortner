package com.kanniappan.urlshortener.controller;

import com.kanniappan.urlshortener.dto.*;
import com.kanniappan.urlshortener.entity.Url;
import com.kanniappan.urlshortener.service.RedisService;
import com.kanniappan.urlshortener.service.UrlService;
import jakarta.validation.Valid;
import org.aspectj.bridge.Message;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.print.attribute.standard.MediaSize;
import java.net.URI;
import java.time.Duration;

@RestController
public class UrlController {
    private final UrlService urlService;

    public UrlController(UrlService urlService, RedisService redisService) {
        this.urlService = urlService;
    }

    @GetMapping("/{shortCode}")
    public ResponseEntity<Void> redirectToOriginalUrl(@PathVariable String shortCode) {
        String originalUrl = urlService.getOriginalUrl(shortCode);
        return ResponseEntity.status(HttpStatus.FOUND).location(URI.create(originalUrl)).build();
    }

    @PostMapping("/api/urls")
    public ResponseEntity<CreateUrlResponse> create(@Valid @RequestBody CreateUrlRequest request) {
        CreateUrlResponse response = urlService.createShortUrl(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/api/urls")
    public ResponseEntity<PagedResponse<UrlResponse>> getUrlsByUser(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "2") int size) {
        System.out.println("control comes into controller");
        PagedResponse<UrlResponse> response = urlService.getMyUrls(page, size);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @DeleteMapping("/api/urls/{shortCode}")
    public ResponseEntity<MessageResponse> deleteUrl(@PathVariable String shortCode) {
        urlService.deleteUrl(shortCode);
        return ResponseEntity.ok(new MessageResponse("Url Deleted Successfully"));
    }

    @PutMapping("/api/urls/{shortCode}")
    public ResponseEntity<MessageResponse> updateUrl(@PathVariable String shortCode, @RequestBody UpdateRequest request) {
        System.out.println("control comes into update controller");
        urlService.updateUrl(shortCode, request);
        return ResponseEntity.ok(new MessageResponse("Url has been updated Successfully"));
    }
}
