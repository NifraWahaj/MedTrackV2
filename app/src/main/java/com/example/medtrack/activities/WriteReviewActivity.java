package com.example.medtrack.activities;

import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.drawable.LayerDrawable;
import android.os.Bundle;
import android.util.Log;
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
import com.example.medtrack.utils.MyFirebaseMessagingService;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessagingService;

import java.util.HashMap;
import java.util.Map;

public class WriteReviewActivity extends AppCompatActivity {

    private RatingBar ratingBar;
    private TextView tvTitle, tvAuthor;
    private EditText etReview;
    private String Blogtitle, review, blogId, author;
    private float userRating;
    private Button btnSubmit, btnDelete;
    private boolean isEditMode, deletedBlog;
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
        tvAuthor = findViewById(R.id.tvAuthor);
        btnDelete = findViewById(R.id.btnDelete);
        // Initialize data
        isEditMode = false;
        deletedBlog = false;
        // Retrieve data from Intent
        if (getIntent() != null) {
            userRating = getIntent().getFloatExtra("userRating", 0);
            Blogtitle = getIntent().getStringExtra("blogTitle");
            blogId = getIntent().getStringExtra("blogId");
            author = getIntent().getStringExtra("author");

            review = getIntent().getStringExtra("reviewText");
            isEditMode = getIntent().getBooleanExtra("editMode", false);
            if (isEditMode == true) {
                String reviewText = getIntent().getStringExtra("reviewText");
                etReview.setText(reviewText);
                btnDelete.setVisibility(View.VISIBLE);


            } else {
                btnDelete.setVisibility(View.GONE);
            }

            // Set the data to views
            tvAuthor.setText(author);
            tvTitle.setText(Blogtitle);
            ratingBar.setRating(userRating);

            // Customize RatingBar
            LayerDrawable stars = (LayerDrawable) ratingBar.getProgressDrawable();
            stars.getDrawable(2).setColorFilter(ContextCompat.getColor(this, R.color.ratingColor), PorterDuff.Mode.SRC_ATOP);
        }

        // Back button listener
        backButton.setOnClickListener(v -> finish());
        btnDelete.setOnClickListener(v -> {
            deleteReview(blogId);

        });
        // Submit button listener
        btnSubmit.setOnClickListener(v -> {
            submitReview();
        });
    }

    private void deleteReview(String blogId) {
        String userId = User.getCurrentUserId(this); // Get userId to use as the key for ratings and reviews
        // Get a reference to the specific blog in Firebase
        DatabaseReference blogRef = FirebaseDatabase.getInstance().getReference("blogs").child(blogId);

        // Get the reference to the specific user's review and rating
        DatabaseReference reviewRef = blogRef.child("reviews_and_ratings").child(userId);

        // Delete the review and rating for the specific user
        reviewRef.removeValue().addOnSuccessListener(aVoid -> {
            deletedBlog = true;
            storeNotificationInCommunity(blogId, userId, "Deleted Review", " deleted their review on your blog");

            notifyBlogAuthor(blogId);
            // Show success message
            Toast.makeText(this, "Review and Rating deleted successfully!", Toast.LENGTH_SHORT).show();
        }).addOnFailureListener(e -> {
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
        blogRef.child("reviews_and_ratings").child(userId).setValue(reviewAndRatingData).addOnSuccessListener(aVoid -> {
            notifyBlogAuthor(blogId);
            if (isEditMode == true) {
                storeNotificationInCommunity(blogId, userId, "New Review", " edited their review on your blog ");

            } else {
                storeNotificationInCommunity(blogId, userId, "New Review", " reviewed your blog");
            }

            Toast.makeText(this, "Review and Rating added to Firebase!", Toast.LENGTH_SHORT).show();
        }).addOnFailureListener(e -> Toast.makeText(this, "Failed to add review and rating", Toast.LENGTH_SHORT).show());
    }

    private void notifyBlogAuthor(String blogId) {
        DatabaseReference blogRef = FirebaseDatabase.getInstance().getReference("blogs").child(blogId);

        Log.d("notifyBlogAuthor", "Starting notifyBlogAuthor for blogId: " + blogId);

        blogRef.get().addOnCompleteListener(task -> {
            if (!task.isSuccessful() || task.getResult() == null) {
                Log.e("notifyBlogAuthor", "Failed to retrieve blog details or task result is null");
                return;
            }

            String authorId = task.getResult().child("userId").getValue(String.class);
            Log.d("notifyBlogAuthor", "Retrieved authorId: " + authorId);

            if (authorId == null) {
                Log.e("notifyBlogAuthor", "Author ID is null");
                return;
            }

            DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users").child(authorId);
            userRef.get().addOnCompleteListener(userTask -> {
                if (!userTask.isSuccessful() || userTask.getResult() == null) {
                    Log.e("notifyBlogAuthor", "Failed to retrieve user details or task result is null");
                    return;
                }

                String fcmToken = userTask.getResult().child("fcmToken").getValue(String.class);
                Log.d("notifyBlogAuthor", "Retrieved FCM Token: " + fcmToken);

                if (fcmToken == null) {
                    Log.e("notifyBlogAuthor", "FCM Token is null");
                    return;
                }
                if (deletedBlog) {
                    sendNotificationToAuthor(fcmToken, "Deleted Review", User.getCurrentUserName(WriteReviewActivity.this) + " deleted their review on your blog ");
                }
                if (isEditMode) {
                    Log.d("notifyBlogAuthor", "Edit mode is true");
                    sendNotificationToAuthor(fcmToken, "New Review", " edited their review on your blog ");
                } else {
                    sendNotificationToAuthor(fcmToken, "New Review", User.getCurrentUserName(WriteReviewActivity.this) + " reviewed your blog ");
                }


            });
        });
    }

    private void sendNotificationToAuthor(String fcmToken, String title, String message) {
        Context context = WriteReviewActivity.this;

        // Use the static method with the context
        MyFirebaseMessagingService.sendNotification(context, message);
        Toast.makeText(this, "Sent", Toast.LENGTH_SHORT).show();
    }


    private void storeNotificationInCommunity(String blogId, String userId, String title, String message) {
        DatabaseReference notificationRef = FirebaseDatabase.getInstance().getReference("communitynotifications");

        // Create a notification object to store
        Map<String, Object> notificationData = new HashMap<>();
        notificationData.put("userId", userId);  // The user who added the review
        notificationData.put("username", User.getCurrentUserName(WriteReviewActivity.this));  // The user who added the review

        notificationData.put("blogId", blogId);  // The blog that received the review
        notificationData.put("title", Blogtitle);    // Title of the notification
        notificationData.put("message", message); // Message of the notification
        notificationData.put("commentText", review); // Message of the notification
        notificationData.put("timestamp", System.currentTimeMillis()); // Timestamp to keep track of when the notification was added

        // Store the notification under a unique ID
        String notificationId = notificationRef.push().getKey(); // Generates a unique ID
        if (notificationId != null) {
            notificationRef.child(notificationId).setValue(notificationData).addOnSuccessListener(aVoid -> Log.d("Notification", "Notification stored successfully")).addOnFailureListener(e -> Log.e("Notification", "Failed to store notification", e));
        }
    }


}
