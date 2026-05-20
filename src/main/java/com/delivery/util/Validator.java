package com.delivery.util;

import java.util.regex.Pattern;

public class Validator {
    private static final Pattern EMAIL_PATTERN =
        Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
    private static final Pattern PHONE_PATTERN =
        Pattern.compile("^[0-9]{9,15}$");

    public static void validateNotEmpty(String value, String fieldName) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException(fieldName + " nu poate fi gol.");
        }
    }

    public static void validateEmail(String email) {
        validateNotEmpty(email, "Email");
        if (!EMAIL_PATTERN.matcher(email.trim()).matches()) {
            throw new IllegalArgumentException("Formatul email-ului este incorect.");
        }
    }

    public static void validatePhone(String phone) {
        validateNotEmpty(phone, "Telefon");
        if (!PHONE_PATTERN.matcher(phone.trim()).matches()) {
            throw new IllegalArgumentException("Telefonul trebuie să conțină 9-15 cifre.");
        }
    }

    public static void validatePositiveNumber(double value, String fieldName) {
        if (value < 0) {
            throw new IllegalArgumentException(fieldName + " nu poate fi negativ.");
        }
    }
}
