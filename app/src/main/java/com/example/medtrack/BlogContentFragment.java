package com.example.medtrack;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.style.ImageSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.StrikethroughSpan;
import android.text.style.StyleSpan;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
    private TextView etTitle, etBlogContent;
    private String blogId;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_blog_content, container, false);

        etTitle = view.findViewById(R.id.etTitle);
        etBlogContent = view.findViewById(R.id.etBlogContent);

        // Retrieve blogId from the arguments
        if (getArguments() != null) {
            blogId = getArguments().getString("blogId");
        }

        fetchBlogContent(blogId);  // Fetch content based on blogId

        return view;
    }

    private void fetchBlogContent(String blogId) {
        // Assuming you're using Firebase to fetch the content
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("blogs").child(blogId);
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String title = snapshot.child("title").getValue(String.class);
                    String json = snapshot.child("content").getValue(String.class);
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
            }

            spannableText.append(spanText);
        }

        // Set the formatted text to the TextView
        etTitle.setText(title);
        etBlogContent.setText(spannableText);
    }

    // Method to convert base64 string to Bitmap
    private Bitmap base64ToBitmap(String base64) {
        byte[] decodedString = Base64.decode(base64, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
    }
}
