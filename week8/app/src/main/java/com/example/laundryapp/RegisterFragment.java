package com.example.laundryapp;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class RegisterFragment extends Fragment {

    private TextInputLayout tilUsername, tilEmail, tilPassword;
    private TextInputEditText etUsername, etEmail, etPassword;
    private DatabaseHelper dbHelper;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.screen_register, container, false);

        dbHelper = new DatabaseHelper(getContext());
        tilUsername = view.findViewById(R.id.tilRegUsername);
        tilEmail = view.findViewById(R.id.tilRegEmail);
        tilPassword = view.findViewById(R.id.tilRegPassword);
        etUsername = view.findViewById(R.id.etRegUsername);
        etEmail = view.findViewById(R.id.etRegEmail);
        etPassword = view.findViewById(R.id.etRegPassword);
        MaterialButton btnRegister = view.findViewById(R.id.btnRegister);

        // Keyboard Handling
        KeyboardHandler.setOnActionDoneListener(etPassword, this::performRegistration);

        btnRegister.setOnClickListener(v -> performRegistration());

        return view;
    }

    private void performRegistration() {
        String username = etUsername.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        tilUsername.setError(null);
        tilEmail.setError(null);
        tilPassword.setError(null);

        // Validation using InputValidator
        if (!InputValidator.isNotEmpty(username)) {
            tilUsername.setError("Username required");
        } else if (!InputValidator.isValidEmail(email)) {
            tilEmail.setError("Valid email required");
        } else if (!InputValidator.isValidPassword(password)) {
            tilPassword.setError("Password too short (min 4 chars)");
        } else {
            EventLogger.logEvent("Registering user: " + username);
            if (dbHelper.addUser(username, password)) {
                EventLogger.showToast(getActivity(), "Account Created Successfully!");
                if (getParentFragmentManager() != null) {
                    getParentFragmentManager().popBackStack();
                }
            } else {
                EventLogger.showToast(getActivity(), "Registration Failed (User might exist)");
            }
        }
    }
}
