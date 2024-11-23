package com.example.medtrack.adapters;

import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.medtrack.R;
import com.example.medtrack.models.Blog;

import java.util.List;

public class BlogAdapter extends RecyclerView.Adapter<BlogAdapter.BlogViewHolder> {
    private List<Blog> blogList;
    private Context context;
    private OnBlogClickListener onBlogClickListener;
    private boolean isUserBlog; // Flag to check if called from usersBlogActivity

    public interface OnBlogClickListener {
        void onBlogClick(Blog blog);
        void onEditBlog(Blog blog);
        void onDeleteBlog(Blog blog);
    }

    public BlogAdapter(Context context, List<Blog> blogList, OnBlogClickListener listener, boolean isUserBlog) {
        this.context = context;
        this.blogList = blogList;
        this.onBlogClickListener = listener;
        this.isUserBlog = isUserBlog; // Set the flag
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

        // Assuming the content is already in HTML format
        String htmlContent = blog.getContent();
        if (htmlContent != null && !htmlContent.isEmpty()) {
            // Remove <img> tags from the HTML content
            htmlContent = htmlContent.replaceAll("<img[^>]*>", "");

            // Split the content into words
            String[] words = htmlContent.split("\\s+");

            // Limit to 30 words
            StringBuilder limitedContent = new StringBuilder();
            for (int i = 0; i < Math.min(30, words.length); i++) {
                limitedContent.append(words[i]).append(" ");
            }

            // Append "..." to indicate more content if there are more than 30 words
            if (words.length > 30) {
                limitedContent.append("...");
            }

            // Display the limited content in the TextView, formatted as HTML
            holder.contentTextView.setText(Html.fromHtml(limitedContent.toString(), Html.FROM_HTML_MODE_LEGACY));
        }

        // Set an OnClickListener that will invoke the onBlogClick function
        holder.itemView.setOnClickListener(v -> {
             if (onBlogClickListener != null) {
                 Toast.makeText(context, "blog adapter item lcike",Toast.LENGTH_SHORT).show();

                 onBlogClickListener.onBlogClick(blog);  // Notify the fragment
            }
        });

        // Control visibility based on the flag
        if (isUserBlog) {
            holder.editButton.setVisibility(View.VISIBLE);
            holder.deleteButton.setVisibility(View.VISIBLE);
        } else {
            holder.editButton.setVisibility(View.GONE);
            holder.deleteButton.setVisibility(View.GONE);
        }

        // Set click listeners for edit and delete
        holder.editButton.setOnClickListener(v -> {
            if (onBlogClickListener != null) {
                onBlogClickListener.onEditBlog(blog);  // Call edit method
            }
        });

        holder.deleteButton.setOnClickListener(v -> {
            if (onBlogClickListener != null) {
                onBlogClickListener.onDeleteBlog(blog);  // Call delete method
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
        ImageView editButton;
        ImageView deleteButton;

        public BlogViewHolder(View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.blogTitle);
            contentTextView = itemView.findViewById(R.id.blogDescription);
            editButton = itemView.findViewById(R.id.ivEdit);  // Add reference to Edit button
            deleteButton = itemView.findViewById(R.id.ivDelete);  // Add reference to Delete button
        }
    }
}
