package com.example.medtrack.activities;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.medtrack.R;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class SignUpActivity extends AppCompatActivity {
    private EditText etEmail, etPassword, etConfirmPassword, etName;
    private TextView tvLogin;
    private Button btnSignUp;
    private FirebaseAuth mAuth;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        // Initialize Firebase Auth instance
        mAuth = FirebaseAuth.getInstance();

        // Bind UI elements
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);
        etName = findViewById(R.id.etName);
        btnSignUp = findViewById(R.id.btnSignUp);
        tvLogin = findViewById(R.id.tvAlreadyHaveAnAccountLogin);
        progressBar = findViewById(R.id.progress_bar);

        // Set up Sign-Up button click listener
        btnSignUp.setOnClickListener(view -> {
            String email = etEmail.getText().toString().trim();
            String password = etPassword.getText().toString().trim();
            String confirmPassword = etConfirmPassword.getText().toString().trim();
            String name = etName.getText().toString().trim();

            // Validation checks
            if (TextUtils.isEmpty(name)) {
                Toast.makeText(SignUpActivity.this, "Name can't be empty", Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.GONE); // Ensure progress bar is hidden

                return;
            }
            if (TextUtils.isEmpty(email)) {
                Toast.makeText(SignUpActivity.this, "Email can't be empty", Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.GONE); // Ensure progress bar is hidden

                return;
            }
            if (TextUtils.isEmpty(password)) {
                Toast.makeText(SignUpActivity.this, "Password can't be empty", Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.GONE); // Ensure progress bar is hidden

                return;
            }
            if (!password.equals(confirmPassword)) {
                Toast.makeText(SignUpActivity.this, "Passwords do not match", Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.GONE); // Ensure progress bar is hidden

                return;
            }

            btnSignUp.setEnabled(false); // Disable the button

            // Show the ProgressBar before starting authentication
            progressBar.setVisibility(View.VISIBLE);

            // Create new user with Firebase Authentication
            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(SignUpActivity.this, task -> {
                        progressBar.setVisibility(View.GONE); // Ensure it's hidden for all cases
                        if (task.isSuccessful()) {
                            Log.d(TAG, "createUserWithEmail:success");
                            Toast.makeText(SignUpActivity.this, "Sign-up successful", Toast.LENGTH_SHORT).show();

                            // Save user to the database
                            saveUserToDatabase(name, email);

                            // Redirect to MainActivity
                            Intent i = new Intent(SignUpActivity.this, MainActivity.class);
                            startActivity(i);
                            finish();
                        } else {
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            Toast.makeText(SignUpActivity.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                        }
                    });

        });

        // Set up Login button click listener
        tvLogin.setOnClickListener(view -> {
            Intent loginIntent = new Intent(SignUpActivity.this, LoginActivity.class);
            startActivity(loginIntent);
            finish(); // Prevent navigating back to SignUpActivity
        });
    }

    private void saveUserToDatabase(String name, String email) {
        // Get the currently authenticated user's unique ID
        String userId = mAuth.getCurrentUser().getUid();

        // Reference to the "users" node in the Firebase Database
        FirebaseDatabase database = FirebaseDatabase.getInstance("https://medtrack-68ec9-default-rtdb.asia-southeast1.firebasedatabase.app");
        DatabaseReference userRef = database.getReference("users").child(userId);

        // Create a map of user data
        Map<String, Object> userData = new HashMap<>();
        userData.put("name", name);
        userData.put("email", email);

        // Save user data under their unique ID
        userRef.setValue(userData)
                .addOnSuccessListener(aVoid -> {
                    Log.d(TAG, "User saved to Firebase Database with ID: " + userId);
                    Toast.makeText(SignUpActivity.this, "User saved successfully", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Failed to save user: " + e.getMessage());
                    Toast.makeText(SignUpActivity.this, "Failed to save user data.", Toast.LENGTH_SHORT).show();
                });
    }
}
