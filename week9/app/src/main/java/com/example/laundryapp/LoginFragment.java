package com.example.laundryapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.bumptech.glide.Glide;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class LoginFragment extends Fragment {

    private TextInputLayout tilUsername;
    private TextInputLayout tilPassword;
    private TextInputEditText etUsername;
    private TextInputEditText etPassword;
    private DatabaseHelper dbHelper;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.screen_login, container, false);

        dbHelper = new DatabaseHelper(getContext());
        tilUsername = view.findViewById(R.id.tilEmail);
        tilPassword = view.findViewById(R.id.tilPassword);
        etUsername = view.findViewById(R.id.etEmail);
        etPassword = view.findViewById(R.id.etPassword);
        ImageView ivLogo = view.findViewById(R.id.ivLogo);
        MaterialButton btnSignIn = view.findViewById(R.id.btnSignIn);
        TextView tvRegister = view.findViewById(R.id.tvRegister);

        if (tilUsername != null) {
            tilUsername.setHint("Username (e.g. admin)");
        }

        if (ivLogo != null) {
            Glide.with(this)
                    .load("https://cdn-icons-png.flaticon.com/512/3003/3003984.png")
                    .into(ivLogo);
        }

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

        KeyboardHandler.setOnActionDoneListener(etPassword, this::attemptLogin);

        if (btnSignIn != null) {
            btnSignIn.setOnClickListener(v -> attemptLogin());
        }

        return view;
    }

    private void attemptLogin() {
        if (etUsername == null || etPassword == null) return;

        String username = etUsername.getText() != null ? etUsername.getText().toString().trim() : "";
        String password = etPassword.getText() != null ? etPassword.getText().toString().trim() : "";

        if (tilUsername != null) tilUsername.setError(null);
        if (tilPassword != null) tilPassword.setError(null);

        if (!InputValidator.isNotEmpty(username)) {
            if (tilUsername != null) tilUsername.setError("Username cannot be empty");
            EventLogger.logEvent("Login failed: empty username");
        } else if (!InputValidator.isNotEmpty(password)) {
            if (tilPassword != null) tilPassword.setError("Password cannot be empty");
            EventLogger.logEvent("Login failed: empty password");
        } else if (!InputValidator.isValidPassword(password)) {
            if (tilPassword != null) tilPassword.setError("Password must be at least 4 characters");
            EventLogger.logEvent("Login failed: password too short");
        } else {
            EventLogger.logEvent("Login attempt for username: " + username);
            
            if (dbHelper.checkUser(username, password)) {
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
