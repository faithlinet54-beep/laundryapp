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

    private TextInputLayout tilUsername, tilPassword;
    private TextInputEditText etUsername, etPassword;
    private DatabaseHelper dbHelper;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.screen_register, container, false);

        dbHelper = new DatabaseHelper(getContext());
        tilUsername = view.findViewById(R.id.tilRegUsername);
        tilPassword = view.findViewById(R.id.tilRegPassword);
        etUsername = view.findViewById(R.id.etRegUsername);
        etPassword = view.findViewById(R.id.etRegPassword);
        MaterialButton btnRegister = view.findViewById(R.id.btnRegister);

        if (btnRegister != null) {
            btnRegister.setOnClickListener(v -> attemptRegister());
        }

        return view;
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
        } else {
            if (dbHelper.addUser(username, password)) {
                EventLogger.showToast(getActivity(), "Registration Successful");
                getParentFragmentManager().popBackStack();
            } else {
                EventLogger.showToast(getActivity(), "Username already exists");
            }
        }
    }
}
