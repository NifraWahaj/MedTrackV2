package com.example.medtrack;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {

    //Hooks
    private EditText etEmail, etPassword;
    private TextView tvSignUp, tvForgotPassword;
    private Button btnLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        init();
        // Set a click listener for the login button
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Retrieve entered username and password
                String email = etEmail.getText().toString();
                String password = etPassword.getText().toString();

                // Implement authentication logic here
                if (email.equals("Admin") && password.equals("123")) {
                    // Successful login
                    Toast.makeText(com.example.medtrack.LoginActivity.this, "Login successful", Toast.LENGTH_SHORT).show();
                } else {
                    // Failed login
                    Toast.makeText(com.example.medtrack.LoginActivity.this, "Invalid username or password", Toast.LENGTH_SHORT).show();
                }
            }
        });


        // If user want to navigate to SignUp Page using Dont have an Account Signup option
        tvSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //    Intent i = new Intent(com.example.medtrack.LoginActivity.this, SignUpActivity.class);
                //    Intent i = new Intent(com.example.medtrack.LoginActivity.this, s.class);
                Intent i = new Intent(com.example.medtrack.LoginActivity.this, MainActivity.class);
                startActivity(i);
                // finish();
            }
        });
        // If user want to fogets password
      /*  tvForgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent i = new Intent(com.example.medtrack.LoginActivity.this, ForgotPasswordActivity.class);
                startActivity(i);
                // finish();
            }
        });*/
        //         tvForgotPassword=findViewById(R.id.tvForgotPassword);


    }
    private void init(){
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        tvSignUp=findViewById(R.id.tvDontHaveAnAccountSignUp);
        tvForgotPassword=findViewById(R.id.tvForgotPassword);
    }

}