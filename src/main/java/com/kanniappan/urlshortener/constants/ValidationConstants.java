package com.kanniappan.urlshortener.constants;

public final class ValidationConstants {

    private ValidationConstants() {
    }

    public static final String PASSWORD_REGEX =
            "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$";
}