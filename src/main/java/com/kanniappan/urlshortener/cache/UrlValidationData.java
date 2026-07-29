package com.kanniappan.urlshortener.cache;

import java.time.LocalDateTime;

public interface UrlValidationData {

    boolean active();

    LocalDateTime expiresAt();
}
