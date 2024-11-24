package com.example.medtrack.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.medtrack.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

@SuppressLint("CustomSplashScreen")
public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        // Load the animations from XML
        Animation leftPillAnimation = AnimationUtils.loadAnimation(this, R.anim.leftpill);
        Animation rightPillAnimation = AnimationUtils.loadAnimation(this, R.anim.rightpill);

        ImageView leftPill = findViewById(R.id.leftPill);
        ImageView rightPill = findViewById(R.id.rightPill);

        // Start the animation on both pills
        leftPill.startAnimation(leftPillAnimation);
        rightPill.startAnimation(rightPillAnimation);

        new Handler().postDelayed(() -> {
            // Check if user is already authenticated
            FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
            if (currentUser != null) {
                // User is logged in, redirect to MainActivity
                Intent mainIntent = new Intent(SplashActivity.this, MainActivity.class);
                startActivity(mainIntent);
            } else {
                // User is not logged in, redirect to LoginActivity
                Intent loginIntent = new Intent(SplashActivity.this, LoginActivity.class);
                startActivity(loginIntent);
            }
            finish();
        }, 2000); // Changed splash duration to 2000ms (2 seconds)
    }
}
