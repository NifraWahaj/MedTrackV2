package com.example.medtrack.adapters;

import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.drawable.LayerDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.medtrack.R;
import com.example.medtrack.models.Review;

import java.util.List;

import android.widget.RatingBar;

public class RatingReviewAdapter extends RecyclerView.Adapter<RatingReviewAdapter.ReviewViewHolder> {

    private List<Review> reviewList;
    private final Context context;

    public RatingReviewAdapter(Context c, List<Review> reviewList) {
        this.reviewList = reviewList;
        this.context = c;
    }

    @NonNull
    @Override
    public ReviewViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate the layout using the updated XML file
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_item_ratings_reviews, parent, false);
        return new ReviewViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ReviewViewHolder holder, int position) {
        Review currentReview = reviewList.get(position);

        // Set the data to the views
        holder.userNameTextView.setText(currentReview.getUserName());
        holder.ratingBar.setRating(currentReview.getRating());
        holder.reviewTextView.setText(currentReview.getReviewText());
        // Change the color of the filled stars in the RatingBar
        LayerDrawable stars = (LayerDrawable) holder.ratingBar.getProgressDrawable();
        stars.getDrawable(2).setColorFilter(ContextCompat.getColor(context, R.color.ratingColor), PorterDuff.Mode.SRC_ATOP);
    }

    @Override
    public int getItemCount() {
        return reviewList.size();
    }

    public static class ReviewViewHolder extends RecyclerView.ViewHolder {
        TextView userNameTextView, reviewTextView;
        RatingBar ratingBar;

        public ReviewViewHolder(View itemView) {
            super(itemView);
            userNameTextView = itemView.findViewById(R.id.TvUsername); // Corrected ID based on your layout
            reviewTextView = itemView.findViewById(R.id.tvReview); // Corrected ID based on your layout
            ratingBar = itemView.findViewById(R.id.reviewRatingBar); // Corrected ID based on your layout
        }
    }
}
