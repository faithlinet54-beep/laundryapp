package com.example.laundryapp;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

/**
 * SplashActivity provides a premium, professional entry point.
 * Features logo animation and transitions to Login.
 */
public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // Set immersive mode or just simple view
        setContentView(R.layout.activity_splash);

        View cardLogo = findViewById(R.id.cardLogo);
        ImageView ivLogo = findViewById(R.id.ivSplashLogo);
        TextView tvAppName = findViewById(R.id.tvAppName);
        TextView tvTagline = findViewById(R.id.tvTagline);

        if (ivLogo != null) {
            ivLogo.setImageResource(R.drawable.ic_app_logo);
        }

        // Professional Entrance Animations
        if (cardLogo != null) {
            cardLogo.setAlpha(0f);
            cardLogo.setScaleX(0.5f);
            cardLogo.setScaleY(0.5f);
            cardLogo.animate()
                    .alpha(1f)
                    .scaleX(1f)
                    .scaleY(1f)
                    .setDuration(1200)
                    .setInterpolator(new AccelerateDecelerateInterpolator())
                    .start();
        }

        if (tvAppName != null) {
            tvAppName.setAlpha(0f);
            tvAppName.setTranslationY(50f);
            tvAppName.animate()
                    .alpha(1f)
                    .translationY(0f)
                    .setDuration(1000)
                    .setStartDelay(500)
                    .start();
        }

        if (tvTagline != null) {
            tvTagline.setAlpha(0f);
            tvTagline.animate()
                    .alpha(1f)
                    .setDuration(1000)
                    .setStartDelay(1000)
                    .start();
        }

        // Hold for 5 seconds then proceed
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            startActivity(new Intent(SplashActivity.this, LoginActivity.class));
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            finish();
        }, 5000);
    }
}
