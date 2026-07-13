package com.example.laundryapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

/**
 * LoginActivity handles user authentication.
 * Connected using Intents as per Requirement 1.
 */
public class LoginActivity extends AppCompatActivity {

    private TextInputLayout tilUsername;
    private TextInputLayout tilPassword;
    private TextInputEditText etUsername;
    private TextInputEditText etPassword;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences prefs = getSharedPreferences("LaundryPrefs", Context.MODE_PRIVATE);
        if (prefs.getBoolean("isLoggedIn", false)) {
            startActivity(new Intent(this, MainActivity.class));
            finish();
            return;
        }

        setContentView(R.layout.screen_login);

        dbHelper = new DatabaseHelper(this);
        tilUsername = findViewById(R.id.tilEmail);
        tilPassword = findViewById(R.id.tilPassword);
        etUsername = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        ImageView ivLogo = findViewById(R.id.ivLogo);
        MaterialButton btnSignIn = findViewById(R.id.btnSignIn);
        TextView tvRegister = findViewById(R.id.tvRegister);

        if (tilUsername != null) {
            tilUsername.setHint("Username (e.g. admin)");
        }

        if (ivLogo != null) {
            ivLogo.setImageResource(R.drawable.ic_app_logo);
        }

        if (tvRegister != null) {
            tvRegister.setOnClickListener(v -> {
                // Navigate to RegisterActivity using Intent
                Intent intent = new Intent(this, RegisterActivity.class);
                startActivity(intent);
            });
        }

        KeyboardHandler.setOnActionDoneListener(etPassword, this::attemptLogin);

        if (btnSignIn != null) {
            btnSignIn.setOnClickListener(v -> attemptLogin());
        }
    }

    private void attemptLogin() {
        if (etUsername == null || etPassword == null) return;

        String username = etUsername.getText() != null ? etUsername.getText().toString().trim() : "";
        String password = etPassword.getText() != null ? etPassword.getText().toString().trim() : "";

        if (tilUsername != null) tilUsername.setError(null);
        if (tilPassword != null) tilPassword.setError(null);

        if (!InputValidator.isNotEmpty(username)) {
            if (tilUsername != null) tilUsername.setError("Username cannot be empty");
        } else if (!InputValidator.isNotEmpty(password)) {
            if (tilPassword != null) tilPassword.setError("Password cannot be empty");
        } else if (!InputValidator.isValidPassword(password)) {
            if (tilPassword != null) tilPassword.setError("Password must be at least 4 characters");
        } else {
            if (dbHelper.checkUser(username, password)) {
                SharedPreferences prefs = getSharedPreferences("LaundryPrefs", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = prefs.edit();
                editor.putBoolean("isLoggedIn", true);
                editor.putString("username", username);
                editor.apply();

                EventLogger.showToast(this, "Login Successful!");

                // Navigate to MainActivity using Intent
                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);
                finish();
            } else {
                EventLogger.showToast(this, "Invalid Username or Password");
            }
        }
    }
}
