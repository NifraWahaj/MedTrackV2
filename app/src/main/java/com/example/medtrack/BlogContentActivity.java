package com.example.medtrack;

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
import android.view.View;
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
    private String blogId, title, reviewText, author,authorEmail;
    private float ratingFromDb;
    private ProgressBar progressBar;

    private ScrollView scrollView;
    private RatingBar ratingBar;
    private boolean isEdit;
    private RatingBar.OnRatingBarChangeListener ratingBarChangeListener;
    private ImageButton backButton, btnReviewList,ivProfilePic;
    private LinearLayout LinearLayoutRateBlog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blog_content); // Use an appropriate activity layout

        // Initialize Views
        etTitle = findViewById(R.id.etTitle);
        etBlogContent = findViewById(R.id.etBlogContent);
        ratingBar = findViewById(R.id.rating);
        scrollView = findViewById(R.id.ScrollViewBlogContent);
        backButton = findViewById(R.id.backButton);
        btnReviewList = findViewById(R.id.btnReviewList);
        tvRateThisBlog = findViewById(R.id.tvRateThisBlog);
        tvWriteAReview = findViewById(R.id.tvWriteAReview);
        tvAuthor = findViewById(R.id.tvAuthor);
        LinearLayoutRateBlog=findViewById(R.id.LinearLayoutRateBlog);
        progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.VISIBLE);
        //ivProfilePic=findViewById(R.id.ivProfilePic);
        isEdit=false;
        // Customize RatingBar Colors
        LayerDrawable stars = (LayerDrawable) ratingBar.getProgressDrawable();
        stars.getDrawable(2).setColorFilter(ContextCompat.getColor(this, R.color.ratingColor), PorterDuff.Mode.SRC_ATOP);

        // Retrieve Intent Data
        if (getIntent() != null) {
            blogId = getIntent().getStringExtra("blogId");
        }

        // Set Listeners
        setupListeners();

        // Fetch Blog Content
        fetchBlogContent(blogId);
        if (authorEmail != null && authorEmail.equals(User.getCurrentUserEmail(this))) {
            LinearLayoutRateBlog.setVisibility(View.GONE);
        }
        if(authorEmail!=null){
            Toast.makeText(this, "author "+authorEmail+ " "+ User.getCurrentUserEmail(this),Toast.LENGTH_SHORT).show();
        }
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
                intent.putExtra("editMode",true);
                intent.putExtra("author",author);

                if(isEdit==true){
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
            intent.putExtra("author",author);
            intent.putExtra("reviewText", reviewText);
            startActivity(intent);
        });

        // Back Button
        backButton.setOnClickListener(v -> onBackPressed());

        // Review List Button
        btnReviewList.setOnClickListener(v -> {
            Intent intent = new Intent(BlogContentActivity.this, RatingsReviewsListActivity.class);
            Toast.makeText(this,"button clicked ",Toast.LENGTH_SHORT).show();

            startActivity(intent);
            Toast.makeText(this,"button start activity ",Toast.LENGTH_SHORT).show();

        });
    }

    private void fetchBlogContent(String blogId) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("blogs").child(blogId);
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    title = snapshot.child("title").getValue(String.class);
                    String json = snapshot.child("content").getValue(String.class);
                    author = snapshot.child("userName").getValue(String.class);
                    authorEmail= snapshot.child("userEmail").getValue(String.class);
                    Toast.makeText(getApplicationContext(),"Auhto email "+authorEmail,Toast.LENGTH_SHORT).show();
                    tvAuthor.setText("by "+author);
                    displayFormattedText(title, json);
                    // Only check after data is retrieved
                    if (authorEmail != null && authorEmail.equals(User.getCurrentUserEmail(BlogContentActivity.this))) {
                        LinearLayoutRateBlog.setVisibility(View.GONE);
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

    private void displayFormattedText(String title, String json) {
        // Parse and display formatted text
        Gson gson = new Gson();
        Type listType = new TypeToken<List<FormattedText>>() {}.getType();
        List<FormattedText> formattedTexts = gson.fromJson(json, listType);

        SpannableStringBuilder spannableText = new SpannableStringBuilder();
        for (FormattedText formattedText : formattedTexts) {
            Spannable spanText;

            if (formattedText.getImageBase64() != null) {
                Bitmap bitmap = base64ToBitmap(formattedText.getImageBase64());
                Drawable drawable = new BitmapDrawable(getResources(), bitmap);
                drawable.setBounds(0, 0, 400, 400);
                ImageSpan imageSpan = new ImageSpan(drawable);

                spanText = new SpannableString(" ");
                spanText.setSpan(imageSpan, 0, 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            } else {
                spanText = new SpannableString(formattedText.getText());
                if (formattedText.isBold()) spanText.setSpan(new StyleSpan(Typeface.BOLD), 0, formattedText.getText().length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                if (formattedText.isItalic()) spanText.setSpan(new StyleSpan(Typeface.ITALIC), 0, formattedText.getText().length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                if (formattedText.isStrikethrough()) spanText.setSpan(new StrikethroughSpan(), 0, formattedText.getText().length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                if (formattedText.getRelativeSize() > 1.0f) spanText.setSpan(new RelativeSizeSpan(formattedText.getRelativeSize()), 0, formattedText.getText().length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                int gravity = formattedText.getAlignment();  // You can define alignment as an int
                switch (gravity) {
                    case 8388611: // Start alignment
                        spanText.setSpan(new AlignmentSpan.Standard(Layout.Alignment.ALIGN_NORMAL),
                                0, spanText.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                        break;
                    case 17: // Center alignment
                        spanText.setSpan(new AlignmentSpan.Standard(Layout.Alignment.ALIGN_CENTER),
                                0, spanText.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                        break;
                    case 8388613: // End alignment
                        spanText.setSpan(new AlignmentSpan.Standard(Layout.Alignment.ALIGN_OPPOSITE),
                                0, spanText.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                        break;
                    case 16777224: // Justify (START | END)
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            etBlogContent.setJustificationMode(LineBreaker.JUSTIFICATION_MODE_INTER_WORD);
                        }
                        break;
                    default: // Fallback for unexpected values
                        spanText.setSpan(new AlignmentSpan.Standard(Layout.Alignment.ALIGN_NORMAL),
                                0, spanText.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                        break;
                }
                if (formattedText.getLink() != null) {
                    final String url = formattedText.getLink();
                    ClickableSpan clickableSpan = new ClickableSpan() {
                        @Override
                        public void onClick(@NonNull View widget) {
                            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                            startActivity(intent);
                        }

                        @Override
                        public void updateDrawState(@NonNull TextPaint ds) {
                            super.updateDrawState(ds);
                            ds.setColor(ContextCompat.getColor(BlogContentActivity.this, android.R.color.holo_blue_dark));
                            ds.setUnderlineText(true);
                        }
                    };
                    spanText.setSpan(clickableSpan, 0, url.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                }
            }
            spannableText.append(spanText);
        }

        etTitle.setText(title);
        etBlogContent.setText(spannableText);
        etBlogContent.setMovementMethod(LinkMovementMethod.getInstance());
        progressBar.setVisibility(View.GONE);  // Hide progress bar on failure
     }

    private Bitmap base64ToBitmap(String base64) {
        byte[] decodedString = Base64.decode(base64, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
    }

    private void checkIfUserHasReviewed(String blogId) {
        String currentUserEmail = User.getCurrentUserEmail(this);
        DatabaseReference reviewsRef = FirebaseDatabase.getInstance().getReference("reviews");

        reviewsRef.orderByChild("userEmail").equalTo(currentUserEmail)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot reviewSnapshot : snapshot.getChildren()) {
                            String reviewBlogId = reviewSnapshot.child("BlogId").getValue(String.class);
                            if (blogId.equals(reviewBlogId)) {
                                reviewText = reviewSnapshot.child("review").getValue(String.class);
                                ratingFromDb = reviewSnapshot.child("rating").getValue(Float.class);

                                ratingBar.setRating(ratingFromDb);
                                tvRateThisBlog.setText("Edit Your Review");
                                tvWriteAReview.setText("Edit Review");

                                return;
                            }
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(BlogContentActivity.this, "Failed to check review status", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
