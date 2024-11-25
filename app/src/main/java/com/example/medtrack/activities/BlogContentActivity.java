package com.example.medtrack.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.graphics.text.LineBreaker;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.text.Layout;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.AlignmentSpan;
import android.text.style.ClickableSpan;
import android.text.style.ImageSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.StrikethroughSpan;
import android.text.style.StyleSpan;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.medtrack.R;
import com.example.medtrack.models.FormattedText;
import com.example.medtrack.models.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;
public class BlogContentActivity extends AppCompatActivity {

    private TextView etTitle, etBlogContent, tvRateThisBlog, tvWriteAReview, tvAuthor;
    private String blogId, title, reviewText, author,authorEmail, blogUserId;
    private float ratingFromDb;
    private ProgressBar progressBar;
    private ScrollView scrollView;
    private RatingBar ratingBar;
    private boolean isEdit;
    private RatingBar.OnRatingBarChangeListener ratingBarChangeListener;
    private ImageButton backButton, btnReviewList, ivProfilePic;
    private WebView webViewContent;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blog_content);

        // Initialize Views
        etTitle = findViewById(R.id.etTitle);
        webViewContent = findViewById(R.id.webViewContent);
        webViewContent.getSettings().setJavaScriptEnabled(true);  // If you need JavaScript support
        webViewContent.getSettings().setLoadsImagesAutomatically(true);  // Ensure images load automatically

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
        stars.getDrawable(2).setColorFilter(ContextCompat.getColor(this, R.color.ratingColor), PorterDuff.Mode.SRC_ATOP);

        // Retrieve Intent Data
        if (getIntent() != null) {
            blogId = getIntent().getStringExtra("blogId");
            blogUserId = getIntent().getStringExtra("userId");
        }
     if(blogUserId.equals(User.getCurrentUserId(BlogContentActivity.this))){
           tvRateThisBlog.setVisibility(View.INVISIBLE);
         tvWriteAReview.setVisibility(View.INVISIBLE);
            ratingBar.setVisibility(View.INVISIBLE);
           findViewById(R.id.tvRatingsAndReviews).setVisibility(View.VISIBLE);
           btnReviewList.setVisibility(View.VISIBLE);

        }
        // Set Listeners
        setupListeners();

        // Fetch Blog Content
        fetchBlogContent(blogId);

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
                intent.putExtra("editMode", true);

                if (isEdit) {
                    intent.putExtra("reviewText", reviewText);
                }

                startActivity(intent);

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
            intent.putExtra("reviewText",reviewText );
            intent.putExtra("author", author);
            intent.putExtra("reviewText", reviewText);
            startActivity(intent);
        });

        // Back Button
        backButton.setOnClickListener(v -> onBackPressed());

        // Review List Button
        btnReviewList.setOnClickListener(v -> {
            Intent intent = new Intent(BlogContentActivity.this, RatingsReviewsListActivity.class);
            Toast.makeText(this, "button  reviews clicked", Toast.LENGTH_SHORT).show();
            intent.putExtra("blogId", blogId);



            startActivity(intent);
            Toast.makeText(this, "button start activity", Toast.LENGTH_SHORT).show();
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
                Toast.makeText(BlogContentActivity.this, "Failed to load blog content", Toast.LENGTH_SHORT).show();
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
                Toast.makeText(BlogContentActivity.this, "Failed to fetch author details", Toast.LENGTH_SHORT).show();
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
                    isEdit=true;

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
                Toast.makeText(BlogContentActivity.this, "Failed to check review status", Toast.LENGTH_SHORT).show();
            }
        });
    }

}
