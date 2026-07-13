package com.example.laundryapp;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

/**
 * RegisterActivity handles new user registration.
 */
public class RegisterActivity extends AppCompatActivity {

    private TextInputLayout tilUsername, tilPassword;
    private TextInputEditText etUsername, etPassword;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.screen_register);

        dbHelper = new DatabaseHelper(this);
        tilUsername = findViewById(R.id.tilRegUsername);
        tilPassword = findViewById(R.id.tilRegPassword);
        etUsername = findViewById(R.id.etRegUsername);
        etPassword = findViewById(R.id.etRegPassword);
        MaterialButton btnRegister = findViewById(R.id.btnRegister);

        if (btnRegister != null) {
            btnRegister.setOnClickListener(v -> attemptRegister());
        }
    }

    private void attemptRegister() {
        if (etUsername == null || etPassword == null) return;

        String username = etUsername.getText() != null ? etUsername.getText().toString().trim() : "";
        String password = etPassword.getText() != null ? etPassword.getText().toString().trim() : "";

        if (tilUsername != null) tilUsername.setError(null);
        if (tilPassword != null) tilPassword.setError(null);

        if (!InputValidator.isNotEmpty(username)) {
            if (tilUsername != null) tilUsername.setError("Username required");
        } else if (!InputValidator.isNotEmpty(password)) {
            if (tilPassword != null) tilPassword.setError("Password required");
        } else if (!InputValidator.isValidPassword(password)) {
            if (tilPassword != null) tilPassword.setError("Password must be at least 4 characters");
        } else {
            if (dbHelper.addUser(username, password)) {
                EventLogger.showToast(this, "Registration Successful");
                finish(); // Go back to LoginActivity
            } else {
                EventLogger.showToast(this, "Username already exists");
            }
        }
    }
}
