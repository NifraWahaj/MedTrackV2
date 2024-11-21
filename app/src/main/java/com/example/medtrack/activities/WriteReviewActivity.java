package com.example.medtrack.activities;

import android.graphics.PorterDuff;
import android.graphics.drawable.LayerDrawable;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.medtrack.R;
import com.example.medtrack.models.User;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class WriteReviewActivity extends AppCompatActivity {

    private RatingBar ratingBar;
    private TextView tvTitle,tvAuthor;
    private EditText etReview;
    private String title, review, blogId,  author;
    private float userRating;
    private Button btnSubmit;
    private boolean isEditMode;
    private ImageButton backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write_review); // Use the same layout as the fragment

        // Initialize Views
        ratingBar = findViewById(R.id.reviewRatingBar);
        tvTitle = findViewById(R.id.tvTitle);
        etReview = findViewById(R.id.etReview);
        btnSubmit = findViewById(R.id.btnSubmit);
        backButton = findViewById(R.id.backButton);
        tvAuthor=findViewById(R.id.tvAuthor);
        // Initialize data
        isEditMode = false;

        // Retrieve data from Intent
        if (getIntent() != null) {
            userRating = getIntent().getFloatExtra("userRating", 0);
            title = getIntent().getStringExtra("blogTitle");
            blogId = getIntent().getStringExtra("blogId");
              author= getIntent().getStringExtra("author");

            isEditMode= getIntent().getBooleanExtra("editMode",false);
            if (isEditMode==true) {
                 String reviewText = getIntent().getStringExtra("etReview");
                etReview.setText(reviewText);
            }

            // Set the data to views
            tvAuthor.setText(author);
            tvTitle.setText(title);
            ratingBar.setRating(userRating);

            // Customize RatingBar
            LayerDrawable stars = (LayerDrawable) ratingBar.getProgressDrawable();
            stars.getDrawable(2).setColorFilter(ContextCompat.getColor(this, R.color.ratingColor), PorterDuff.Mode.SRC_ATOP);
        }

        // Back button listener
        backButton.setOnClickListener(v -> finish());

        // Submit button listener
        btnSubmit.setOnClickListener(v -> {
            submitReview();
        finish();
        }
        );
    }

    private void submitReview() {
        userRating = ratingBar.getRating();
        review = etReview.getText().toString();
       if(userRating==0.0f){
           Toast.makeText(this,"Please add ratings ro procees",Toast.LENGTH_SHORT).show();
           return;

       }
        // Initialize Firebase
        FirebaseDatabase database = FirebaseDatabase.getInstance("https://medtrack-68ec9-default-rtdb.asia-southeast1.firebasedatabase.app");
        DatabaseReference reviewsRef = database.getReference("reviews");

        Map<String, Object> reviewData = new HashMap<>();
        reviewData.put("BlogTitle", title);
        reviewData.put("BlogId", blogId);
        reviewData.put("rating", userRating);
        reviewData.put("review", review);

        reviewData.put("userEmail", User.getCurrentUserEmail(this));
        reviewData.put("name", User.getCurrentUserName(this));

        if (isEditMode) {
            // Update existing review
            reviewsRef.orderByChild("userEmail").equalTo(User.getCurrentUserName(this)).get().addOnCompleteListener(task -> {
                if (task.isSuccessful() && task.getResult().exists()) {
                    String reviewKey = task.getResult().getChildren().iterator().next().getKey();
                    if (reviewKey != null) {
                        reviewsRef.child(reviewKey).updateChildren(reviewData)
                                .addOnSuccessListener(aVoid -> Toast.makeText(this, "Review updated!", Toast.LENGTH_SHORT).show())
                                .addOnFailureListener(e -> Toast.makeText(this, "Failed to update review", Toast.LENGTH_SHORT).show());
                    }
                }
            });
        } else {
            // Create a new review
            reviewsRef.push().setValue(reviewData)
                    .addOnSuccessListener(aVoid -> Toast.makeText(this, "Review added to Firebase!", Toast.LENGTH_SHORT).show())
                    .addOnFailureListener(e -> Toast.makeText(this, "Failed to add review", Toast.LENGTH_SHORT).show());
        }
    }
}
