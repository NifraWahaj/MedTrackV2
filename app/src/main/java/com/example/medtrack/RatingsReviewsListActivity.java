package com.example.medtrack;

import android.graphics.PorterDuff;
import android.graphics.drawable.LayerDrawable;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class RatingsReviewsListActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private RatingReviewAdapter adapter;
    private List<Review> reviewList;
    private TextView tvRating, tvReviews;
    private RatingBar reviewRatingBar;
    private ImageButton backButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ratings_review_list);

        // Initialize Views
        recyclerView = findViewById(R.id.recyclerViewReviews);
        tvRating = findViewById(R.id.tvRating);
        tvReviews = findViewById(R.id.tvReviews);
        reviewRatingBar = findViewById(R.id.reviewRatingBar);
        backButton = findViewById(R.id.backButton);

        // Set up RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Customize RatingBar Stars
        LayerDrawable stars = (LayerDrawable) reviewRatingBar.getProgressDrawable();
        stars.getDrawable(2).setColorFilter(
                ContextCompat.getColor(this, R.color.ratingColor),
                PorterDuff.Mode.SRC_ATOP
        );

        // Initialize List and Adapter
        reviewList = new ArrayList<>();
        adapter = new RatingReviewAdapter(this, reviewList);
        recyclerView.setAdapter(adapter);
        backButton.setOnClickListener(v -> finish());
Toast.makeText(this,"inside ratingsrviewlsit ",Toast.LENGTH_SHORT).show();
        // Fetch Reviews from Firebase
        fetchReviewsFromFirebase();
    }

    private void fetchReviewsFromFirebase() {
        FirebaseDatabase database = FirebaseDatabase.getInstance("https://medtrack-68ec9-default-rtdb.asia-southeast1.firebasedatabase.app");
        DatabaseReference reviewsRef = database.getReference("reviews");

        reviewsRef.addValueEventListener(new com.google.firebase.database.ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                reviewList.clear(); // Clear the list to avoid duplicates

                for (DataSnapshot reviewSnapshot : snapshot.getChildren()) {
                    String blogTitle = reviewSnapshot.child("BlogTitle").getValue(String.class);
                    String blogId = reviewSnapshot.child("BlogId").getValue(String.class);
                    float rating = reviewSnapshot.child("rating").getValue(Float.class);
                    String reviewText = reviewSnapshot.child("review").getValue(String.class);
                    String userEmail = reviewSnapshot.child("userEmail").getValue(String.class);
                    String userName = reviewSnapshot.child("name").getValue(String.class);

                    // Add each review to the list
                    reviewList.add(new Review(userName, rating, reviewText, blogTitle, userEmail));
                }

                // Update UI
                tvReviews.setText(reviewList.size() + " Reviews");
                calculateRatingsAverage();
                adapter.notifyDataSetChanged(); // Notify adapter of data changes
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(RatingsReviewsListActivity.this, "Failed to fetch reviews: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void calculateRatingsAverage() {
        if (reviewList == null || reviewList.isEmpty()) {
            reviewRatingBar.setRating(0.0f);
            return; // Return if there are no reviews
        }

        float sum = 0.0f;
        for (Review review : reviewList) {
            sum += review.getRating();
        }

        sum = sum / reviewList.size();
        tvRating.setText(sum + " Ratings");
        reviewRatingBar.setRating(sum);
    }
}
