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
}
