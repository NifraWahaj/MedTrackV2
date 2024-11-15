package com.example.medtrack;

import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.style.RelativeSizeSpan;
import android.text.style.StrikethroughSpan;
import android.text.style.StyleSpan;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.List;


public class BlogAdapter extends RecyclerView.Adapter<BlogAdapter.BlogViewHolder> {
    private List<Blog> blogList;
    private Context context;
    private OnBlogClickListener onBlogClickListener;
    SpannableStringBuilder spannableText;
    int wordCount;

    public interface OnBlogClickListener {
        void onBlogClick(Blog blog);
    }

    public BlogAdapter(Context context, List<Blog> blogList, OnBlogClickListener onBlogClickListener) {
        this.context = context;
        this.blogList = blogList;
        this.onBlogClickListener = onBlogClickListener;
    }

    @NonNull
    @Override
    public BlogViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.single_item_community, parent, false);
        return new BlogViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BlogViewHolder holder, int position) {
        Blog blog = blogList.get(position);
        holder.titleTextView.setText(blog.getTitle());
        displayFormattedText(blog.getTitle(), blog.getContent());
        holder.contentTextView.setText(spannableText);

        // Set an OnClickListener that will invoke the onBlogClick function
        holder.itemView.setOnClickListener(v -> {
            if (onBlogClickListener != null) {
                onBlogClickListener.onBlogClick(blog);  // Notify the fragment
            }
        });
    }

    @Override
    public int getItemCount() {
        return blogList.size();
    }

    public class BlogViewHolder extends RecyclerView.ViewHolder {
        TextView titleTextView;
        TextView contentTextView;

        public BlogViewHolder(View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.blogTitle);
            contentTextView = itemView.findViewById(R.id.blogDescription);
        }
    }

    private void displayFormattedText(String title, String json) {
        Gson gson = new Gson();
        Type listType = new TypeToken<List<FormattedText>>() {
        }.getType();
        List<FormattedText> formattedTexts = gson.fromJson(json, listType);
        Log.d("JSON Content", json);

        spannableText = new SpannableStringBuilder();
        int wordCount = 0;
        StringBuilder currentWord = new StringBuilder();

        // Loop through the formatted text
        for (FormattedText formattedText : formattedTexts) {
            // Skip headings
            if (formattedText.getHeadingLevel() > 0) {
                continue;
            }

            // Skip if it's an image (check the content type)
            if (formattedText.getImageBase64() != null) {
                continue;  // Skip image content
            }

            // Collect characters into words
            String text = formattedText.getText();
            if (text.equals(" ")) {
                // Append a space if it's a space character
                if (currentWord.length() > 0) {
                    appendWordToSpannable(currentWord.toString());
                    currentWord.setLength(0);
                }
                spannableText.append(" ");
                continue;
            }

            // Append the current character to the current word
            currentWord.append(text);

            // If the wordCount reaches 30, stop processing
            if (wordCount >= 30) {
                break;
            }
        }

        // If there's any remaining word, append it
             appendWordToSpannable("........");

    }

    // Helper method to append the word (without formatting)
    private void appendWordToSpannable(String word) {
        spannableText.append(word);

        // Increment the word count after appending the word
        wordCount++;
    }
}

