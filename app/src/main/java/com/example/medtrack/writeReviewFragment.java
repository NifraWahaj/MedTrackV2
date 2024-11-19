package com.example.medtrack;

import android.graphics.PorterDuff;
import android.graphics.drawable.LayerDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.util.Log;
import android.widget.ImageButton;
import android.widget.Toast;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class writeReviewFragment extends Fragment {

    private RatingBar ratingBar;
    private TextView tvTitle;
    private EditText etReview;
    private  String title,review,blogId;
    private    float userRating;
    private Button btnSubmit;
    private Boolean isEditMode;
    private ImageButton backButton;
    public writeReviewFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_write_review, container, false);
        isEditMode=false;
        // Initialize RatingBar and TextView
        ratingBar = rootView.findViewById(R.id.reviewRatingBar);
        tvTitle=rootView.findViewById(R.id.tvTitle);
        etReview= rootView.findViewById(R.id.etReview);
        btnSubmit=rootView.findViewById(R.id.btnSubmit);
        backButton= rootView.findViewById(R.id.backButton);
        // Get the rating passed from BlogPostFragment
        // Handle back button click to navigate back to BlogContentFragment

        backButton.setOnClickListener(v -> {


            getParentFragmentManager().popBackStack("WRITE_REVIEW_FRAGMENT", FragmentManager.POP_BACK_STACK_INCLUSIVE);
            return;


        });

        if (getArguments() != null) {
              userRating = getArguments().getFloat("userRating");
              title= getArguments().getString("blogTitle","Users Blog");
          blogId=getArguments().getString( "blogId");
            String tvValue=getArguments().getString("etValue");


            if(tvValue.equals("Edit your Review")){
                isEditMode=true;
                String reviewText=getArguments().getString("etReview");
                etReview.setText(reviewText);
              }

            // Set the received rating to the RatingBar in WriteReviewFragment
            tvTitle.setText(title);
            ratingBar.setRating(userRating);
            // Get the progress drawable
            LayerDrawable stars = (LayerDrawable) ratingBar.getProgressDrawable();

// Change the color of the filled stars
            stars.getDrawable(2).setColorFilter(ContextCompat.getColor(getContext(), R.color.ratingColor), PorterDuff.Mode.SRC_ATOP);
            // Retrieve blogId from the arguments
         }


        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Toast.makeText(getActivity(), "Button clicked!", Toast.LENGTH_SHORT).show();
                userRating= ratingBar.getRating();
                review=etReview.getText().toString();
                FirebaseDatabase database = FirebaseDatabase.getInstance("https://medtrack-68ec9-default-rtdb.asia-southeast1.firebasedatabase.app");
                DatabaseReference reviewsRef = database.getReference("reviews");
                Map<String, Object> reviewData = new HashMap<>();
                //user id and user name
                reviewData.put("BlogTitle", title);
                reviewData.put("BlogId", blogId);
                reviewData.put("rating", userRating);
                reviewData.put("review",review);
                reviewData.put("userEmail",UserUtils.getUserEmail(getContext()));
                reviewData.put("name",UserUtils.getUserName(getContext()));
                if (isEditMode) {
                    // Update the existing review if in edit mode (based on userEmail)
                    reviewsRef.orderByChild("userEmail").equalTo(UserUtils.getUserEmail(getContext())).get().addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            if (task.getResult().exists()) {
                                String reviewKey = task.getResult().getChildren().iterator().next().getKey();
                                if (reviewKey != null) {
                                    // Update the existing review data
                                    reviewsRef.child(reviewKey).updateChildren(reviewData)
                                            .addOnSuccessListener(aVoid -> Toast.makeText(getContext(), "Review updated!", Toast.LENGTH_SHORT).show())
                                            .addOnFailureListener(e -> Toast.makeText(getContext(), "Failed to update review", Toast.LENGTH_SHORT).show());
                                }
                            }
                        }
                    });
                } else {
                    // Create a new review if not in edit mode
                    reviewsRef.push().setValue(reviewData)
                            .addOnSuccessListener(aVoid -> Toast.makeText(getContext(), "Review added to Firebase!", Toast.LENGTH_SHORT).show())
                            .addOnFailureListener(e -> Toast.makeText(getContext(), "Failed to add review", Toast.LENGTH_SHORT).show());
                }
            }

             });


        return rootView;
    }
}
