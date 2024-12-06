package com.example.medtrack.activities;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.medtrack.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ChangePassword extends AppCompatActivity {

    // Declare variables for the views
    private ImageView logo;
    private TextView tvWelcomeToMedTrac;
    public EditText etEmail, etNewPassword, etCnewPassword;
    private Button btnLogin;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        // Initialize view hooks
        logo = findViewById(R.id.logo);
        tvWelcomeToMedTrac = findViewById(R.id.tvWelcomeToMedTrac);
        etEmail = findViewById(R.id.etEmail);
        etNewPassword = findViewById(R.id.etNewPassword);
        etCnewPassword = findViewById(R.id.etCnewPassword);
        btnLogin = findViewById(R.id.btnLogin);

        // Set up click listener for the "Change Password" button
        btnLogin.setOnClickListener(v -> handleChangePassword());
    }

    /**
     * Handles the change password functionality.
     */
    private void handleChangePassword() {
        // Retrieve input values
        String newPassword = etNewPassword.getText().toString().trim();
        String confirmNewPassword = etCnewPassword.getText().toString().trim();

        // Validate inputs
        if (newPassword.isEmpty() || confirmNewPassword.isEmpty()) {
            Toast.makeText(this, "All fields are required!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!newPassword.equals(confirmNewPassword)) {
            Toast.makeText(this, "New Password and Confirm Password do not match!", Toast.LENGTH_SHORT).show();
            return;
        }

        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            user.updatePassword(newPassword)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(this, "Password changed successfully!", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(this, "Failed to change password!", Toast.LENGTH_SHORT).show();
                        }
                    });
        } else {
            Toast.makeText(this, "No authenticated user found!", Toast.LENGTH_SHORT).show();
        }
    }
}
