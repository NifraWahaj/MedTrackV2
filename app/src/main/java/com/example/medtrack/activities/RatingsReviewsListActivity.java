package com.example.medtrack.activities;

import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.LayerDrawable;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.medtrack.R;
import com.example.medtrack.adapters.RatingReviewAdapter;
import com.example.medtrack.models.Review;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RatingsReviewsListActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private RatingReviewAdapter adapter;
    private List<Review> reviewList;
    private TextView tvRating, tvReviews;
    private RatingBar reviewRatingBar;
    private ImageButton backButton;

String blogId;
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
             Intent i= getIntent();
        Toast.makeText(this, "inside  reviews clicked", Toast.LENGTH_SHORT).show();

        blogId=     i.getStringExtra("blogId");
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
         // Fetch Reviews from Firebase
        getAllReviewsAndRatings(blogId);
     }

    private void getAllReviewsAndRatings(String blogId) {
        DatabaseReference blogRef = FirebaseDatabase.getInstance().getReference("blogs").child(blogId);

        blogRef.child("reviews_and_ratings").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    // Step 1: Gather all user IDs
                    List<String> userIds = new ArrayList<>();
                    Map<String, Review> tempReviewMap = new HashMap<>();

                    for (DataSnapshot reviewSnapshot : snapshot.getChildren()) {
                        String userId = reviewSnapshot.getKey();
                        String review = reviewSnapshot.child("review").getValue(String.class);
                        Float rating = reviewSnapshot.child("rating").getValue(Float.class);

                        if (userId != null) {
                            userIds.add(userId);
                            tempReviewMap.put(userId, new Review(userId, rating, review));
                        }
                    }

                    // Step 2: Fetch all usernames in one query
                    fetchAllUserNames(userIds, new UserNamesCallback() {
                        @Override
                        public void onUserNamesFetched(Map<String, String> userNameMap) {
                            // Step 3: Combine user data with reviews
                            for (String userId : userNameMap.keySet()) {
                                Review review = tempReviewMap.get(userId);
                                if (review != null) {
                                    review.setUserName(userNameMap.get(userId));
                                    reviewList.add(review);
                                }
                            }
                            calculateRatingsAverage();
                            // Update the RecyclerView
                            adapter.notifyDataSetChanged();
                        }
                    });
                } else {
                    Log.d("BlogContent", "No reviews found for this blog.");
                    adapter.notifyDataSetChanged(); // Notify for empty list
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
             }
        });
    }

    // Helper method to fetch all usernames in a single query
    private void fetchAllUserNames(List<String> userIds, UserNamesCallback callback) {
        DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("users");

        usersRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Map<String, String> userNameMap = new HashMap<>();

                for (String userId : userIds) {
                    if (snapshot.child(userId).exists()) {
                        String userName = snapshot.child(userId).child("name").getValue(String.class);
                        if (userName != null) {
                            userNameMap.put(userId, userName);
                        }
                    }
                }

                callback.onUserNamesFetched(userNameMap);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                 callback.onUserNamesFetched(new HashMap<>()); // Return empty map on error
            }
        });
    }

    // Callback interface for fetching all usernames
    public interface UserNamesCallback {
        void onUserNamesFetched(Map<String, String> userNameMap);
    }




    private void calculateRatingsAverage() {
        if (reviewList == null || reviewList.isEmpty()) {
            reviewRatingBar.setRating(0.0f);
            Toast.makeText(this," no reviews   "+ reviewList.size(),Toast.LENGTH_SHORT).show();

            return; // Return if there are no reviews
        }
Toast.makeText(this," ratign avaregr "+ reviewList.size(),Toast.LENGTH_SHORT).show();
        float sum = 0.0f;
        for (Review review : reviewList) {
            sum += review.getRating();
        }

        float average = sum / reviewList.size();
        // Round to 2 decimal places
        average = Math.round(average * 100.0f) / 100.0f;

        // Update the UI with the rounded average rating
        tvRating.setText(average + " Ratings");
        reviewRatingBar.setRating(average);
    }

}
