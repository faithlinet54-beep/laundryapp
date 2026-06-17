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

        // Update labels to "Username" if they were "Email"
        tilUsername.setHint("Username (e.g. admin)");

        Glide.with(this)
                .load("https://cdn-icons-png.flaticon.com/512/3003/3003984.png")
                .into(ivLogo);

        btnSignIn.setOnClickListener(v -> {
            String username = etUsername.getText().toString().trim();
            String password = etPassword.getText().toString().trim();

            tilUsername.setError(null);
            tilPassword.setError(null);

            if (username.isEmpty()) {
                tilUsername.setError("Username cannot be empty");
            } else if (password.isEmpty()) {
                tilPassword.setError("Password cannot be empty");
            } else {
                // 10. Password Security - Check user with hashed password in SQLite
                // The checkUser method hashes the input password before comparing
                if (dbHelper.checkUser(username, password)) {
                    
                    // 9. SharedPreferences - Store login status and username
                    if (getActivity() != null) {
                        SharedPreferences prefs = getActivity().getSharedPreferences("LaundryPrefs", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = prefs.edit();
                        editor.putBoolean("isLoggedIn", true);
                        editor.putString("username", username);
                        editor.apply();

                        Toast.makeText(getActivity(), "Login Successful!", Toast.LENGTH_SHORT).show();

                        if (getActivity() instanceof MainActivity) {
                            ((MainActivity) getActivity()).revealMainApplicationFlow();
                        }
                    }
                } else {
                    Toast.makeText(getActivity(), "Invalid Username or Password", Toast.LENGTH_SHORT).show();
                    // Tip: Use admin / admin123
                }
            }
        });

        return view;
    }
}
