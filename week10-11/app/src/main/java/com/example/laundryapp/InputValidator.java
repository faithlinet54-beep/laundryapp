package com.example.laundryapp;

import android.util.Patterns;

public class InputValidator {

    public static boolean isValidEmail(String email) {
        return email != null && Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    public static boolean isValidPassword(String password) {
        return password != null && password.length() >= 4;
    }

    public static boolean isNotEmpty(String text) {
        return text != null && !text.trim().isEmpty();
    }

    public static boolean isValidPhone(String phone) {
        return phone != null && Patterns.PHONE.matcher(phone).matches() && phone.length() >= 10;
    }

    public static boolean isPositive(String value) {
        try {
            double d = Double.parseDouble(value);
            return d > 0;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}
