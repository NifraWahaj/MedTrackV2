package com.example.medtrack.activities;

import android.graphics.PorterDuff;
import android.graphics.drawable.LayerDrawable;
import android.os.Bundle;
import android.view.View;
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
    private Button btnSubmit,btnDelete;
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
        btnDelete=findViewById(R.id.btnDelete);
        // Initialize data
        isEditMode = false;

        // Retrieve data from Intent
        if (getIntent() != null) {
            userRating = getIntent().getFloatExtra("userRating", 0);
            title = getIntent().getStringExtra("blogTitle");
            blogId = getIntent().getStringExtra("blogId");
            author= getIntent().getStringExtra("author");
            review= getIntent().getStringExtra("reviewText");
             isEditMode= getIntent().getBooleanExtra("editMode",false);
            if (isEditMode==true) {
                 String reviewText = getIntent().getStringExtra("reviewText");
                etReview.setText(reviewText);
                btnDelete.setVisibility(View.VISIBLE);


            }
            else{
                btnDelete.setVisibility(View.GONE);
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
        btnDelete.setOnClickListener(v->{
            deleteReview(blogId);
            finish();

        });
        // Submit button listener
        btnSubmit.setOnClickListener(v -> {
            submitReview();
        finish();
        }
        );
    }

 private void  deleteReview(String blogId){
      String userId = User.getCurrentUserId(this); // Get userId to use as the key for ratings and reviews
         // Get a reference to the specific blog in Firebase
         DatabaseReference blogRef = FirebaseDatabase.getInstance().getReference("blogs").child(blogId);

         // Get the reference to the specific user's review and rating
         DatabaseReference reviewRef = blogRef.child("reviews_and_ratings").child(userId);

         // Delete the review and rating for the specific user
         reviewRef.removeValue()
                 .addOnSuccessListener(aVoid -> {
                     // Show success message
                     Toast.makeText(this, "Review and Rating deleted successfully!", Toast.LENGTH_SHORT).show();
                 })
                 .addOnFailureListener(e -> {
                     // Show failure message
                     Toast.makeText(this, "Failed to delete review and rating", Toast.LENGTH_SHORT).show();
                 });
     }




    private void submitReview() {
        userRating = ratingBar.getRating();
        review = etReview.getText().toString();
        String userId = User.getCurrentUserId(this); // Get userId to use as the key for ratings and reviews

        if (userRating == 0.0f) {
            Toast.makeText(this, "Please add ratings to proceed", Toast.LENGTH_SHORT).show();
            return;
        }
        DatabaseReference blogRef = FirebaseDatabase.getInstance().getReference("blogs").child(blogId);

        // Create a map to store both the review and rating data
        Map<String, Object> reviewAndRatingData = new HashMap<>();
        reviewAndRatingData.put("review", review);  // Store the user's review
        reviewAndRatingData.put("rating", userRating);  // Store the user's rating

        // Save both review and rating together under the specific blogId and userId
        blogRef.child("reviews_and_ratings").child(userId).setValue(reviewAndRatingData)
                .addOnSuccessListener(aVoid ->
                        Toast.makeText(this, "Review and Rating added to Firebase!", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Failed to add review and rating", Toast.LENGTH_SHORT).show());
    }



}
