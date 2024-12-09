package com.example.medtrack.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PorterDuff;
import android.graphics.drawable.LayerDrawable;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.medtrack.R;
import com.example.medtrack.fragments.ProfileFragment;
import com.example.medtrack.models.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class BlogContentActivity extends AppCompatActivity {

    private TextView etTitle, etBlogContent, tvRateThisBlog, tvWriteAReview, tvAuthor;
    private String blogId, title, reviewText, author, authorEmail, blogUserId;
    private float ratingFromDb;
    private ProgressBar progressBar;
    private ScrollView scrollView;
    private RatingBar ratingBar;
    private boolean isEdit;
    private RatingBar.OnRatingBarChangeListener ratingBarChangeListener;
    private ImageButton backButton, btnReviewList;
    private WebView webViewContent;
    private ImageView ivProfilePic;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blog_content);

        // Initialize Views
        etTitle = findViewById(R.id.etTitle);
        webViewContent = findViewById(R.id.webViewContent);
        webViewContent.getSettings().setJavaScriptEnabled(true);
        webViewContent.getSettings().setLoadsImagesAutomatically(true);

        ivProfilePic = findViewById(R.id.ivProfilePic);
        ratingBar = findViewById(R.id.rating);
        scrollView = findViewById(R.id.ScrollViewBlogContent);
        backButton = findViewById(R.id.backButton);
        btnReviewList = findViewById(R.id.btnReviewList);
        tvRateThisBlog = findViewById(R.id.tvRateThisBlog);
        tvWriteAReview = findViewById(R.id.tvWriteAReview);
        tvAuthor = findViewById(R.id.tvAuthor);

        progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.VISIBLE);
        isEdit = false;

        // Customize RatingBar Colors
        LayerDrawable stars = (LayerDrawable) ratingBar.getProgressDrawable();
        // ContextCompact ensures compatibility for resource access.
        stars.getDrawable(2).setColorFilter(ContextCompat.getColor(this, R.color.ratingColor), PorterDuff.Mode.SRC_ATOP);

        // Retrieve Intent Data
        if (getIntent() != null) {
            blogId = getIntent().getStringExtra("blogId");
            blogUserId = getIntent().getStringExtra("userId");
            User user = User.fetchUserFromDatabase(blogUserId);
            tvAuthor.setText(user.getName());
        }
        if (blogUserId.equals(User.getCurrentUserId(BlogContentActivity.this))) {
            tvRateThisBlog.setVisibility(View.INVISIBLE);
            tvWriteAReview.setVisibility(View.INVISIBLE);
            ratingBar.setVisibility(View.INVISIBLE);
            findViewById(R.id.tvRatingsAndReviews).setVisibility(View.VISIBLE);
            btnReviewList.setVisibility(View.VISIBLE);

        } else {
            tvRateThisBlog.setVisibility(View.VISIBLE);
            tvWriteAReview.setVisibility(View.VISIBLE);
            ratingBar.setVisibility(View.VISIBLE);
            findViewById(R.id.tvRatingsAndReviews).setVisibility(View.VISIBLE);
            btnReviewList.setVisibility(View.VISIBLE);
        }
        // Set Listeners
        setupListeners();
        // Fetch Blog Content
        fetchBlogContent(blogId);

        loadImageFromFirebase();

        // Check if the current user has reviewed this blog
        checkIfUserHasReviewed(blogId);
    }

    private void setupListeners() {
        // RatingBar Change Listener
        ratingBarChangeListener = (ratingBar, rating, fromUser) -> {
            if (fromUser) {
                ratingBar.setOnRatingBarChangeListener(null); // Temporarily disable
                Intent intent = new Intent(this, WriteReviewActivity.class); // Navigate to WriteReviewActivity
                intent.putExtra("userRating", rating);
                intent.putExtra("blogTitle", title);
                intent.putExtra("blogId", blogId);
                intent.putExtra("editMode", isEdit);

                if (isEdit) {
                    intent.putExtra("reviewText", reviewText);
                }

                startActivity(intent);
               // rating bar is triggered only once
                ratingBar.postDelayed(() -> ratingBar.setOnRatingBarChangeListener(ratingBarChangeListener), 500); // Re-enable
            }
        };
        ratingBar.setOnRatingBarChangeListener(ratingBarChangeListener);

        // Write/Edit Review Listener
        tvWriteAReview.setOnClickListener(v -> {
            Intent intent = new Intent(this, WriteReviewActivity.class);
            intent.putExtra("userRating", ratingFromDb);
            intent.putExtra("blogTitle", title);
            intent.putExtra("blogId", blogId);
            intent.putExtra("editMode", isEdit);
            intent.putExtra("editMode", isEdit);
            intent.putExtra("userRating", ratingBar.getRating());
            intent.putExtra("reviewText", reviewText);
            intent.putExtra("author", author);
            intent.putExtra("reviewText", reviewText);
            startActivity(intent);
        });

        // Back Button to go back to previous activity
        backButton.setOnClickListener(v -> onBackPressed());

        // Review List Button
        btnReviewList.setOnClickListener(v -> {
            Intent intent = new Intent(BlogContentActivity.this, RatingsReviewsListActivity.class);
             intent.putExtra("blogId", blogId);


            startActivity(intent);
        });
    }

    private void fetchBlogContent(String blogId) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("blogs").child(blogId);
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    title = snapshot.child("title").getValue(String.class);
                    String html = snapshot.child("content").getValue(String.class);
                    String userId = snapshot.child("userId").getValue(String.class);  // Get the userId of the author

                    // Fetch the author's name and email from the User class using the userId
                    fetchAuthorDetails(userId);
                    etTitle.setText(title);

                    if (html != null) {
                        webViewContent.loadDataWithBaseURL(null, html, "text/html", "UTF-8", null);
                        progressBar.setVisibility(View.GONE);
                    } else {
                        etBlogContent.setText("No content available.");
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                progressBar.setVisibility(View.GONE);  // Hide progress bar on failure
                Log.e("BlogContentActivity","Failed to load blog content");

             }
        });
    }

    private void fetchAuthorDetails(String userId) {
        // Assuming User class has a method to fetch user details by userId
        DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("users").child(userId);
        usersRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String userName = snapshot.child("name").getValue(String.class);  // Get the name
                    authorEmail = snapshot.child("email").getValue(String.class);  // Get the email
                    author = userName;  // Set the author's name to the TextView

                    tvAuthor.setText("by " + author);  // Display the author's name
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("BlogContentActivity","Failed to fetch author details");
             }
        });
    }

    private void checkIfUserHasReviewed(String blogId) {
        String currentUserId = User.getCurrentUserId(this);  // Fetch the current user's ID
        DatabaseReference blogRef = FirebaseDatabase.getInstance().getReference("blogs").child(blogId);

        // Check if the user has already submitted a review and rating
        blogRef.child("reviews_and_ratings").child(currentUserId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    // Retrieve the review and rating for the current user
                    Log.d("BlogContent", "Review and rating found!");
                    reviewText = snapshot.child("review").getValue(String.class);
                    Float existingRating = snapshot.child("rating").getValue(Float.class);
                    isEdit = true;

                    if (existingRating != null) {
                        ratingBar.setRating(existingRating);  // Set the existing rating in the RatingBar
                    }

                    // Change UI text to indicate the user can edit the review
                    tvRateThisBlog.setText("Edit Your Review");
                    tvWriteAReview.setText("Edit Review");
                } else {
                    // If no review exists, allow the user to add a new review
                    tvRateThisBlog.setText("Rate this Blog");
                    tvWriteAReview.setText("Write a Review");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("BlogContentActivity","Failed to check review  details");

             }
        });
    }

    public void loadImageFromFirebase() {

        FirebaseDatabase database = FirebaseDatabase.getInstance("https://medtrack-68ec9-default-rtdb.asia-southeast1.firebasedatabase.app");
        DatabaseReference userRef = database.getReference("profileImages").child(blogUserId).child("image");

        userRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                String base64Image = task.getResult().getValue(String.class);

                if (base64Image != null) {
                    Bitmap decodedBitmap = decodeBase64ToBitmap(base64Image);

                    if (decodedBitmap != null) {
                        // Set the decoded Bitmap to the ImageView
                        ivProfilePic.setImageBitmap(decodedBitmap);
                     } else {
                        Log.e("DecodeError", "Failed to decode Base64 image.");
                        setDefaultImage();
                    }
                } else {
                    Log.e("Firebase", "No image found for user.");
                    setDefaultImage();
                }
            } else {
                Log.e("Firebase", "Failed to fetch image: " + task.getException().getMessage());
                setDefaultImage();
            }
        });
    }

    private void setDefaultImage() {
        ivProfilePic.setImageResource(R.drawable.user_profile); // Replace `default_image` with your drawable resource name
     }
    public static Bitmap decodeBase64ToBitmap(String base64Image) {
        try {
            // Decode the Base64 string into a byte array
            byte[] decodedString = Base64.decode(base64Image, Base64.DEFAULT);

            // Decode the byte array into a Bitmap
            return BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
        } catch (IllegalArgumentException e) {
            // Handle the error if Base64 string is invalid
            e.printStackTrace();
            return null;
        } catch (OutOfMemoryError e) {
            // Handle the error if the image is too large
            e.printStackTrace();
            return null;
        }
    }

}