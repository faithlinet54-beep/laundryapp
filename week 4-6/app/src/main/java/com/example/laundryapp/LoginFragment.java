package com.example.laundryapp;

import android.os.Bundle;
import android.util.Patterns;
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
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class LoginFragment extends Fragment {

    private TextInputLayout tilEmail;
    private TextInputLayout tilPassword;
    private TextInputEditText etEmail;
    private TextInputEditText etPassword;
    private ImageView ivLogo;
    private TextView tvForgotPassword;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.screen_login, container, false);

        tilEmail = view.findViewById(R.id.tilEmail);
        tilPassword = view.findViewById(R.id.tilPassword);
        etEmail = view.findViewById(R.id.etEmail);
        etPassword = view.findViewById(R.id.etPassword);
        ivLogo = view.findViewById(R.id.ivLogo);
        tvForgotPassword = view.findViewById(R.id.tvForgotPassword);
        MaterialButton btnSignIn = view.findViewById(R.id.btnSignIn);

        // Load attractive logo from internet
        String logoUrl = "https://cdn-icons-png.flaticon.com/512/3003/3003984.png";
        Glide.with(this)
                .load(logoUrl)
                .placeholder(android.R.drawable.ic_menu_slideshow)
                .into(ivLogo);

        btnSignIn.setOnClickListener(v -> {
            String email = etEmail.getText().toString().trim();
            String password = etPassword.getText().toString().trim();

            tilEmail.setError(null);
            tilPassword.setError(null);

            if (email.isEmpty()) {
                tilEmail.setError("Email cannot be empty");
            } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                tilEmail.setError("Please enter a valid email address");
            } else if (password.isEmpty()) {
                tilPassword.setError("Password cannot be empty");
            } else if (password.length() < 4) {
                tilPassword.setError("Password must be at least 4 characters");
            } else {
                Toast.makeText(getActivity(), "Login Successful!", Toast.LENGTH_SHORT).show();

                if (getActivity() instanceof MainActivity) {
                    ((MainActivity) getActivity()).revealMainApplicationFlow();
                }
            }
        });

        tvForgotPassword.setOnClickListener(v -> showForgotPasswordDialog());

        return view;
    }

    private void showForgotPasswordDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Reset Password");
        builder.setMessage("Enter your email address to receive a verification link.");

        final EditText input = new EditText(getActivity());
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        input.setLayoutParams(lp);
        input.setHint("Email Address");
        builder.setView(input);

        builder.setPositiveButton("Send Link", (dialog, which) -> {
            String email = input.getText().toString().trim();
            if (!email.isEmpty() && Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                Toast.makeText(getActivity(), "Verification link sent to " + email, Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(getActivity(), "Please enter a valid email", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());

        builder.show();
    }
}
