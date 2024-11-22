package com.example.medtrack.activities;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.content.Intent;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.medtrack.R;

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


        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                // Start the main activity after the splash screen duration
                Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        }, 2000);// changed from 4000 to 2000 temp
    }
}
