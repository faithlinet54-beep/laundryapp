package com.example.laundryapp;

import android.os.Bundle;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class LoginFragment extends Fragment {

    private EditText emailField;
    private EditText passwordField;
    private Button signInButton;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.screen_login, container, false);

        // Link the XML components to Java variables
        emailField = view.findViewById(R.id.email_input);
        passwordField = view.findViewById(R.id.password_input);
        signInButton = view.findViewById(R.id.btn_sign_in);

        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = emailField.getText().toString().trim();
                String password = passwordField.getText().toString().trim();

                // Validation: Check if inputs accept standard random email/password requirements
                if (email.isEmpty()) {
                    emailField.setError("Email cannot be empty");
                } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    emailField.setError("Please enter a valid email address");
                } else if (password.isEmpty()) {
                    passwordField.setError("Password cannot be empty");
                } else if (password.length() < 4) {
                    passwordField.setError("Password must be at least 4 characters");
                } else {
                    // Success feedback message
                    Toast.makeText(getActivity(), "Login Successful!", Toast.LENGTH_SHORT).show();

                    // Navigate smoothly to the DashboardFragment
                    if (getActivity() != null) {
                        getActivity().getSupportFragmentManager().beginTransaction()
                                .replace(R.id.fragment_container, new DashboardFragment())
                                .commit();

                        // Sync the bottom navigation selection indicator to the Dashboard tab
                        BottomNavigationView bottomNav = getActivity().findViewById(R.id.bottom_navigation);
                        if (bottomNav != null) {
                            bottomNav.setSelectedItemId(R.id.nav_dashboard);
                        }
                    }
                }
            }
        });

        return view;
    }
}