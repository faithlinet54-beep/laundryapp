package com.example.laundryapp;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.Toast;
import com.google.android.material.snackbar.Snackbar;

public class EventLogger {
    private static final String TAG = "LaundryAppEvent";

    public static void logEvent(String message) {
        Log.d(TAG, message);
    }

    public static void showToast(Context context, String message) {
        if (context != null) {
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
        }
    }

    public static void showSnackbar(View view, String message) {
        if (view != null) {
            Snackbar.make(view, message, Snackbar.LENGTH_SHORT).show();
        }
    }
}
