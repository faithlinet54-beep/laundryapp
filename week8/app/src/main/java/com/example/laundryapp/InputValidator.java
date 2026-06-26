package com.example.laundryapp;

import android.text.TextUtils;
import android.util.Patterns;

public class InputValidator {

    public static boolean isValidEmail(String email) {
        return !TextUtils.isEmpty(email) && Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    public static boolean isValidPassword(String password) {
        return !TextUtils.isEmpty(password) && password.length() >= 4;
    }

    public static boolean isNotEmpty(String text) {
        return !TextUtils.isEmpty(text);
    }
}
