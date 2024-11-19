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
    private ImageButton backButton;
    public writeReviewFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_write_review, container, false);

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
              userRating = getArguments().getFloat("userRating", 0f);
              title= getArguments().getString("blogTitle","Users Blog");
          blogId=getArguments().getString( "blogId");

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
                DatabaseReference blogsref = database.getReference("reviews");
                Map<String, Object> reviewData = new HashMap<>();
                //user id and user name
                reviewData.put("BlogTitle", title);
                reviewData.put("BlogId", blogId);
                reviewData.put("rating", userRating);
                reviewData.put("review",review);
                blogsref.push().setValue(reviewData)
                        .addOnSuccessListener(aVoid -> Toast.makeText(getContext(), "Blog saved to Firebase!", Toast.LENGTH_SHORT).show())
                        .addOnFailureListener(e -> Toast.makeText(getContext(), "Failed to save blog", Toast.LENGTH_SHORT).show());
            }

             }
        );
        return rootView;
    }
}
