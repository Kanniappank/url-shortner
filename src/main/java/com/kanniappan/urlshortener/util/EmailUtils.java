package com.kanniappan.urlshortener.util;

public final class EmailUtils {
    public static String normalize(String email) {
        return email == null ? null : email.trim().toLowerCase();
    }
}
