package com.example.medtrack;

import android.graphics.PorterDuff;
import android.graphics.drawable.LayerDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class RatingsReviewsListFragment extends Fragment {

    private RecyclerView recyclerView;
    private RatingReviewAdapter adapter;
    private List<Review> reviewList;
    private TextView tvRating,tvReviews;
    private RatingBar reviewRatingBar;
     @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_ratings_reviews_list, container, false);

        // Initialize RecyclerView
        recyclerView = view.findViewById(R.id.recyclerViewReviews);
         tvRating=view.findViewById(R.id.tvRating);
         tvReviews=view.findViewById(R.id.tvReviews);
         reviewRatingBar=view.findViewById(R.id.reviewRatingBar);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
         LayerDrawable stars = (LayerDrawable) reviewRatingBar.getProgressDrawable();

// Change the color of the filled stars
         stars.getDrawable(2).setColorFilter(ContextCompat.getColor(getContext(), R.color.ratingColor), PorterDuff.Mode.SRC_ATOP);
        // Initialize list and adapter
        reviewList = new ArrayList<>();
        adapter = new RatingReviewAdapter(getContext(),reviewList);
        recyclerView.setAdapter(adapter);

        // Fetch reviews from Firebase
        fetchReviewsFromFirebase();

        return view;
    }

    private void fetchReviewsFromFirebase() {
        FirebaseDatabase database = FirebaseDatabase.getInstance("https://medtrack-68ec9-default-rtdb.asia-southeast1.firebasedatabase.app");
        DatabaseReference reviewsRef = database.getReference("reviews");

        reviewsRef.addValueEventListener(new ValueEventListener() {
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
                tvReviews.setText(reviewList.size()+ " Reviews");
                calculateRatingsAverage();
                adapter.notifyDataSetChanged(); // Notify adapter of data changes
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Failed to fetch reviews: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void calculateRatingsAverage() {
        if (reviewList == null || reviewList.isEmpty()) {
            reviewRatingBar.setRating(0.0f);
            return ;
            // Return  if there are no reviews
        }

        float sum = 0.0f;
        for (Review review : reviewList) {
            sum += review.getRating();
        }
           sum=sum / reviewList.size();
        tvRating.setText(sum+ " Ratings");

        reviewRatingBar.setRating(sum);

    }

}
