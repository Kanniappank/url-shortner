package com.kanniappan.urlshortener.exception;

public class UrlInactiveException extends RuntimeException {
    public UrlInactiveException(String message) {
        super(message);
    }
}
