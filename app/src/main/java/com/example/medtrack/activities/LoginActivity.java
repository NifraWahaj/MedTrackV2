package com.example.medtrack.activities;

import static android.content.ContentValues.TAG;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.medtrack.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class LoginActivity extends AppCompatActivity {

    // Firebase authentication instance
    private FirebaseAuth mAuth;
    private EditText etEmail, etPassword;
    private String userId;
    private Button btnLogin;
    private TextView tvSignUp, tvForgotPassword;

    private ProgressBar progressBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Initialize Firebase Auth instance
        mAuth = FirebaseAuth.getInstance();

        // Bind UI elements
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        tvSignUp = findViewById(R.id.tvDontHaveAnAccountSignUp);
        tvForgotPassword = findViewById(R.id.tvForgotPassword);
        progressBar = findViewById(R.id.progress_bar);

        // Set a click listener for the login button
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = etEmail.getText().toString().trim();
                String password = etPassword.getText().toString().trim();

                if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    Toast.makeText(LoginActivity.this, "Invalid email format", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (password.length() < 6) {
                    Toast.makeText(LoginActivity.this, "Password must be at least 6 characters", Toast.LENGTH_SHORT).show();
                    return;
                }


                // Show the ProgressBar before starting authentication
                progressBar.setVisibility(View.VISIBLE);

                mAuth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                // Always hide ProgressBar once the task is complete
                                progressBar.setVisibility(View.GONE);

                                if (task.isSuccessful()) {
                                    Log.d(TAG, "signInWithEmail:success");
                                    Toast.makeText(LoginActivity.this, "Login successful", Toast.LENGTH_SHORT).show();
                                    fetchUserFromDatabase(email);

                                    // Redirect to MainActivity
                                    Intent i = new Intent(LoginActivity.this, MainActivity.class);
                                    startActivity(i);
                                    finish();
                                } else {
                                    Log.w(TAG, "signInWithEmail:failure", task.getException());
                                    Toast.makeText(LoginActivity.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });


        // Set up click listener for Sign-Up button to navigate to SignUpActivity
        tvSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent signUpIntent = new Intent(LoginActivity.this, SignUpActivity.class);
                startActivity(signUpIntent);
            }
        });

        // Set up click listener for Forgot Password (if implemented)
        tvForgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
              //  Intent forgotPasswordIntent = new Intent(LoginActivity.this, ForgotPasswordActivity.class);
             //   startActivity(forgotPasswordIntent);

                Intent tempLogin = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(tempLogin);
                finish();
            }
        });
    }

    private void fetchUserFromDatabase(String email) {
        // Get reference to users in Firebase Realtime Database
        DatabaseReference userRef = FirebaseDatabase.getInstance()
                .getReference("users");

        // Query the users by email
        userRef.orderByChild("email").equalTo(email)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                // Fetch user data
                                // Fetch user ID
                                  userId = snapshot.getKey(); // Get the unique user ID
                                String name = dataSnapshot.child("name").getValue(String.class);
                                String email = dataSnapshot.child("email").getValue(String.class);
                                String base64Image = dataSnapshot.child("image").getValue(String.class);

                                // Store user data in SharedPreferences
                                storeUserInSharedPreferences(name, email,base64Image);

                                // Redirect to MainActivity after successful login
                                Intent mainIntent = new Intent(LoginActivity.this, MainActivity.class);
                                startActivity(mainIntent);
                                finish();
                            }
                        } else {
                            Toast.makeText(LoginActivity.this, "No user found with this email.", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Toast.makeText(LoginActivity.this, "Error fetching user data.", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void storeUserInSharedPreferences(String name, String email , String image) {
        SharedPreferences sharedPreferences = getSharedPreferences("user_pref", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        // Clear all stored preferences
        editor.clear();
        editor.putString("name", name);
        editor.putString("userId", userId); // Store user ID

        editor.putString("email", email);
        editor.putString("image", image);
        editor.apply();
    }
}
