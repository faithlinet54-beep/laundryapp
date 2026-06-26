package com.example.laundryapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.bumptech.glide.Glide;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import android.widget.ImageView;
import android.widget.TextView;

public class LoginFragment extends Fragment {

    private TextInputLayout tilUsername;
    private TextInputLayout tilPassword;
    private TextInputEditText etUsername;
    private TextInputEditText etPassword;
    private ImageView ivLogo;
    private DatabaseHelper dbHelper;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.screen_login, container, false);

        dbHelper = new DatabaseHelper(getContext());
        tilUsername = view.findViewById(R.id.tilEmail); // Using existing IDs
        tilPassword = view.findViewById(R.id.tilPassword);
        etUsername = view.findViewById(R.id.etEmail);
        etPassword = view.findViewById(R.id.etPassword);
        ivLogo = view.findViewById(R.id.ivLogo);
        MaterialButton btnSignIn = view.findViewById(R.id.btnSignIn);
        TextView tvRegister = view.findViewById(R.id.tvRegister);

        // Update labels to "Username" if they were "Email"
        tilUsername.setHint("Username (e.g. admin)");

        Glide.with(this)
                .load("https://cdn-icons-png.flaticon.com/512/3003/3003984.png")
                .into(ivLogo);

        // Navigate to Register
        if (tvRegister != null) {
            tvRegister.setOnClickListener(v -> {
                if (getActivity() != null) {
                    getActivity().getSupportFragmentManager().beginTransaction()
                            .replace(R.id.fragment_container, new RegisterFragment())
                            .addToBackStack(null)
                            .commit();
                }
            });
        }

        // Keyboard Handling using the reusable KeyboardHandler class
        KeyboardHandler.setOnActionDoneListener(etPassword, this::attemptLogin);

        btnSignIn.setOnClickListener(v -> attemptLogin());

        return view;
    }

    private void attemptLogin() {
        String username = etUsername.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        tilUsername.setError(null);
        tilPassword.setError(null);

        // Input Validation using the reusable InputValidator class
        if (!InputValidator.isNotEmpty(username)) {
            tilUsername.setError("Username cannot be empty");
            EventLogger.logEvent("Login failed: empty username");
        } else if (!InputValidator.isNotEmpty(password)) {
            tilPassword.setError("Password cannot be empty");
            EventLogger.logEvent("Login failed: empty password");
        } else if (!InputValidator.isValidPassword(password)) {
            tilPassword.setError("Password must be at least 4 characters");
            EventLogger.logEvent("Login failed: password too short");
        } else {
            EventLogger.logEvent("Login attempt for username: " + username);
            
            if (dbHelper.checkUser(username, password)) {
                // SharedPreferences - Store login status and username
                if (getActivity() != null) {
                    SharedPreferences prefs = getActivity().getSharedPreferences("LaundryPrefs", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = prefs.edit();
                    editor.putBoolean("isLoggedIn", true);
                    editor.putString("username", username);
                    editor.apply();

                    EventLogger.showToast(getActivity(), "Login Successful!");

                    if (getActivity() instanceof MainActivity) {
                        ((MainActivity) getActivity()).revealMainApplicationFlow();
                    }
                }
            } else {
                EventLogger.showToast(getActivity(), "Invalid Username or Password");
                EventLogger.logEvent("Login failed: incorrect credentials for " + username);
            }
        }
    }
}
