package com.example.medtrack;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ImageSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.StrikethroughSpan;
import android.text.style.StyleSpan;
import android.text.style.URLSpan;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.RatingBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;

public class BlogContentFragment extends Fragment {
    private TextView etTitle, etBlogContent,tvRateThisBlog,tvWriteAReview,tvAuthor;
    private String blogId,title,reviewText, author;
     float ratingFromDb;

    private ScrollView scrollView;
    private RatingBar ratingBar;
    private RatingBar.OnRatingBarChangeListener ratingBarChangeListener;
    private ImageButton backButton,btnReviewList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_blog_content, container, false);

        etTitle = view.findViewById(R.id.etTitle);
        etBlogContent = view.findViewById(R.id.etBlogContent);
        ratingBar = view.findViewById(R.id.rating);
        scrollView= view.findViewById(R.id.ScrollViewBlogContent);
        backButton=view.findViewById(R.id.backButton);
        btnReviewList=view.findViewById(R.id.btnReviewList);
        tvRateThisBlog=view.findViewById(R.id.tvRateThisBlog);
        tvWriteAReview=view.findViewById(R.id.tvWriteAReview);
        tvAuthor=view.findViewById(R.id.tvAuthor);
        // Get the progress drawable
        LayerDrawable stars = (LayerDrawable) ratingBar.getProgressDrawable();

// Change the color of the filled stars
        stars.getDrawable(2).setColorFilter(ContextCompat.getColor(getContext(), R.color.ratingColor), PorterDuff.Mode.SRC_ATOP);
        // Retrieve blogId from the arguments
        if (getArguments() != null) {
            blogId = getArguments().getString("blogId");
        }
        checkIfUserHasReviewed(blogId);
         ratingBarChangeListener = (ratingBar, rating, fromUser) -> {
            if (fromUser) {  // Ensure the change was user-initiated
                ratingBar.setOnRatingBarChangeListener(null);  // Temporarily disable
                Bundle bundle = new Bundle();
                bundle.putFloat("userRating", rating);
                bundle.putString("blogTitle", title);
                bundle.putString("blogId", blogId);
                writeReviewFragment writeReviewFragment = new writeReviewFragment();
                writeReviewFragment.setArguments(bundle);
              //  scrollView.setVisibility(View.GONE);

                getParentFragmentManager().beginTransaction()
                        .replace(R.id.main_container, writeReviewFragment, "WRITE_REVIEW_FRAGMENT")
                        .addToBackStack("WRITE_REVIEW_FRAGMENT")
                        .commit();

                ratingBar.postDelayed(() -> ratingBar.setOnRatingBarChangeListener(ratingBarChangeListener), 500);  // Re-enable after a short delay
            }
        };
        ratingBar.setOnRatingBarChangeListener(ratingBarChangeListener);

        tvWriteAReview.setOnClickListener(V->
        {                Bundle bundle = new Bundle();

            bundle.putFloat("userRating", 0.0f);
            bundle.putString("blogTitle", title);
            bundle.putString("blogId", blogId);
            if(tvWriteAReview.getText().equals("Edit your Review")){
                bundle.putString("etValue","Edit your Review");
                bundle.putFloat("userRating",ratingFromDb);
                bundle.putString("etReview",reviewText);
            }
            else{
                bundle.putString("etValue","Write a Review");

            }
            writeReviewFragment writeReviewFragment = new writeReviewFragment();
            writeReviewFragment.setArguments(bundle);

            getParentFragmentManager().beginTransaction()
                    .replace(R.id.main_container, writeReviewFragment, "WRITE_REVIEW_FRAGMENT")
                    .addToBackStack("WRITE_REVIEW_FRAGMENT")
                    .commit();
        });
        fetchBlogContent(blogId);  // Fetch content based on blogId
// Check the fragment back stack size
        getParentFragmentManager().addOnBackStackChangedListener(() -> {
            if (getParentFragmentManager().getBackStackEntryCount() == 1) {
                 ratingBar.setRating(0.0f);  // Reset the rating to 0.0f (no rating)
              //  ratingBar.setVisibility(View.GONE);
                scrollView.setVisibility(View.VISIBLE);

            }
        });
        backButton.setOnClickListener(v -> {
         getActivity().onBackPressed();
          //  getParentFragmentManager().popBackStack("BLOG_CONTENT_FRAGMENT", FragmentManager.POP_BACK_STACK_INCLUSIVE);
           //return;


        });
        btnReviewList.setOnClickListener(v -> {
            getParentFragmentManager().beginTransaction()
                    .replace(R.id.main_container, new RatingsReviewsListFragment(), "REVIEW_LIST_FRAGMENT")
                    .addToBackStack("REVIEW_LIST_FRAGMENT")
                    .commit();


        });

        return view;
    }
    @Override
    public void onResume() {
        super.onResume();
        Log.d("BlogContentFragment", "onResume called");

        // Reset RatingBar and visibility when coming back
        ratingBar.setRating(0.0f);  // Reset the rating to 0.0f (no rating)
        scrollView.setVisibility(View.VISIBLE);  // Ensure the content is visible
    }

    private void fetchBlogContent(String blogId) {
        // Assuming you're using Firebase to fetch the content
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("blogs").child(blogId);
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (snapshot.exists()) {
                      title = snapshot.child("title").getValue(String.class);
                    String json = snapshot.child("content").getValue(String.class);
                      author= snapshot.child("userName").getValue(String.class) ;
                      tvAuthor.setText(author);
                    displayFormattedText(title, json);
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Handle error
            }
        });
    }

    private void displayFormattedText(String title, String json) {
        // Parse the JSON content to get the formatted text
        Gson gson = new Gson();
        Type listType = new TypeToken<List<FormattedText>>() {
        }.getType();
        List<FormattedText> formattedTexts = gson.fromJson(json, listType);

        SpannableStringBuilder spannableText = new SpannableStringBuilder();

        for (FormattedText formattedText : formattedTexts) {
             Spannable spanText;

            if (formattedText.getImageBase64() != null) {
                // Decode the image from base64 and add it to the spannable text
                Bitmap bitmap = base64ToBitmap(formattedText.getImageBase64());
                Drawable drawable = new BitmapDrawable(getResources(), bitmap);
                drawable.setBounds(0, 0, 400, 400);
                ImageSpan imageSpan = new ImageSpan(drawable);

                spanText = new SpannableString(" ");
                spanText.setSpan(imageSpan, 0, 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            } else {
                spanText = new SpannableString(formattedText.getText());

                // Apply formatting
                if (formattedText.isBold()) {
                    spanText.setSpan(new StyleSpan(Typeface.BOLD), 0, formattedText.getText().length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                }
                if (formattedText.isItalic()) {
                    spanText.setSpan(new StyleSpan(Typeface.ITALIC), 0, formattedText.getText().length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                }
                if (formattedText.isStrikethrough()) {
                    spanText.setSpan(new StrikethroughSpan(), 0, formattedText.getText().length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                }
                if (formattedText.getRelativeSize() > 1.0f) {
                    spanText.setSpan(new RelativeSizeSpan(formattedText.getRelativeSize()), 0, formattedText.getText().length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                }
                // Handle clickable links

                // Handle clickable links using ClickableSpan
                if (formattedText.getLink() != null) {
                    final String url = formattedText.getLink();
                    SpannableString spannableString = new SpannableString(url);

                    // Create ClickableSpan
                    ClickableSpan clickableSpan = new ClickableSpan() {
                        @Override
                        public void onClick(@NonNull View widget) {
                            // Open the URL in Chrome explicitly
                            String url = widget.getTag().toString();  // Ensure URL is correctly passed to the method
                            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                            intent.setPackage("com.android.chrome");  // Target Chrome
                            try {
                            // Check if Chrome is installed
                            if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
                                try {
                                    startActivity(intent);  // Launch Chrome if installed
                                } catch (Exception e) {
                                    e.printStackTrace();
                                    // Fallback to default browser if there's an error
                                    Intent defaultIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                                    if (getActivity() != null) { // Make sure getActivity() is not null
                                        startActivity(defaultIntent);
                                    }
                                }
                            } else {
                                // If Chrome is not installed, open the default browser
                                Intent defaultIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                                if (getActivity() != null) { // Ensure the activity is not null
                                    startActivity(defaultIntent);
                                }
                            }}
                            catch (Exception e) {
                                e.printStackTrace();
                                Toast.makeText(getContext(), "Unable to open link: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }



                        @Override
                        public void updateDrawState(@NonNull TextPaint ds) {
                            super.updateDrawState(ds);
                            ds.setColor(ContextCompat.getColor(getContext(), android.R.color.holo_blue_dark));  // Set the color to blue
                            ds.setUnderlineText(true);  // Set the text to be underlined
                        }
                    };

                    // Apply the clickable span to the entire URL
                    spannableString.setSpan(clickableSpan, 0, url.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    spanText = spannableString;
                }
            }

            spannableText.append(spanText);
        }

        // Set the formatted text to the TextView


        etTitle.setText(title);
        etBlogContent.setText(spannableText);
        etBlogContent.setMovementMethod(LinkMovementMethod.getInstance());
         etBlogContent.setClickable(true); // Make sure the TextView itself is clickable
        etBlogContent.setFocusableInTouchMode(true); // Make sure the TextView can handle touch interactions

    }

    // Method to convert base64 string to Bitmap
    private Bitmap base64ToBitmap(String base64) {
        byte[] decodedString = Base64.decode(base64, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
    }


    private void checkIfUserHasReviewed(String blogId) {
        String currentUserEmail = UserUtils.getUserEmail(getContext()); // Get the current user's email
        DatabaseReference reviewsRef = FirebaseDatabase.getInstance()
                .getReference("reviews");

        reviewsRef.orderByChild("userEmail").equalTo(currentUserEmail)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        boolean hasReviewed = false;

                        for (DataSnapshot reviewSnapshot : snapshot.getChildren()) {
                            String reviewBlogId = reviewSnapshot.child("BlogId").getValue(String.class);
                            if (blogId.equals(reviewBlogId)) {
                                hasReviewed = true;
                                // Retrieve review details
                                  reviewText = reviewSnapshot.child("review").getValue(String.class);
                                ratingFromDb = reviewSnapshot.child("rating").getValue(Float.class);

                                // Display the existing review
                                displayExistingReview(reviewText, ratingFromDb);
                                break;
                            }
                        }


                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(getContext(), "Failed to check reviews", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void displayExistingReview(String reviewText, float rating) {
        // Set the existing review and rating to the views
        ratingBar.setOnRatingBarChangeListener(null); // Disable the listener
        tvWriteAReview.setText("Edit your Review");
         ratingBar.setRating(rating);

        // Optionally, disable further edits
        ratingBar.setIsIndicator(true); // Make RatingBar read-only
        tvRateThisBlog.setText("You rated This blog");

     }



}
