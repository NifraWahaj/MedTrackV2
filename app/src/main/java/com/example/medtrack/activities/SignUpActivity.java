package com.example.medtrack.activities;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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

        // Set up Sign-Up button click listener
        btnSignUp.setOnClickListener(view -> {
            String email = etEmail.getText().toString().trim();
            String password = etPassword.getText().toString().trim();
            String confirmPassword = etConfirmPassword.getText().toString().trim();
            String name = etName.getText().toString().trim();

            // Validation checks
            if (TextUtils.isEmpty(name)) {
                Toast.makeText(SignUpActivity.this, "Name can't be empty", Toast.LENGTH_SHORT).show();
                return;
            }
            if (TextUtils.isEmpty(email)) {
                Toast.makeText(SignUpActivity.this, "Email can't be empty", Toast.LENGTH_SHORT).show();
                return;
            }
            if (TextUtils.isEmpty(password)) {
                Toast.makeText(SignUpActivity.this, "Password can't be empty", Toast.LENGTH_SHORT).show();
                return;
            }
            if (!password.equals(confirmPassword)) {
                Toast.makeText(SignUpActivity.this, "Passwords do not match", Toast.LENGTH_SHORT).show();
                return;
            }

            // Create new user with Firebase Authentication
            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(SignUpActivity.this, task -> {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "createUserWithEmail:success");
                            Toast.makeText(SignUpActivity.this, "Sign-up successful", Toast.LENGTH_SHORT).show();

                            // Save user to the database
                            saveUserToDatabase(name, email);

                            // Redirect to MainActivity and finish this activity
                            Intent i = new Intent(SignUpActivity.this, MainActivity.class);
                            startActivity(i);
                            finish(); // Prevent navigating back to SignUpActivity
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
        FirebaseDatabase database = FirebaseDatabase.getInstance("https://medtrack-68ec9-default-rtdb.asia-southeast1.firebasedatabase.app");
        DatabaseReference userRef = database.getReference("users");

        Map<String, Object> userData = new HashMap<>();
        userData.put("name", name);
        userData.put("email", email);

        userRef.push().setValue(userData)
                .addOnSuccessListener(aVoid -> Log.d(TAG, "User saved to Firebase Database."))
                .addOnFailureListener(e -> Log.e(TAG, "Failed to save user: " + e.getMessage()));
    }
}
