package com.example.medtrack.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.medtrack.R;
import com.example.medtrack.activities.BlogContentActivity;
import com.example.medtrack.activities.EditBlogAPIActivity;
import com.example.medtrack.activities.RatedBlogsActivity;
import com.example.medtrack.adapters.BlogAdapter;
import com.example.medtrack.models.Blog;
import com.example.medtrack.activities.usersBlogActivity;
import com.google.android.material.chip.Chip;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class CommunityFragment extends Fragment implements BlogAdapter.OnBlogClickListener {

    private RecyclerView recyclerView;
    private BlogAdapter blogAdapter;
    private List<Blog> blogList = new ArrayList<>();
    private List<Blog> filteredBlogList = new ArrayList<>();
    private Button btnSearch;
    private EditText etSearch;
    private ProgressBar progressBar;

    private LinearLayout linearLayoutCommunity;
    private Chip chipYourBlogs, chipRatedBlogs, chipWriteBlog;

    public CommunityFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_community, container, false);
        progressBar = view.findViewById(R.id.progressBar); // Initialize ProgressBar

        linearLayoutCommunity = view.findViewById(R.id.linearLayoutCommunity);
        recyclerView = view.findViewById(R.id.recyclerViewCommunity);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        // Initialize the BlogAdapter and pass the OnBlogClickListener
        blogAdapter = new BlogAdapter(getContext(), filteredBlogList, this, false);
        recyclerView.setAdapter(blogAdapter);
        chipYourBlogs = view.findViewById(R.id.chipYourBlogs);
        chipRatedBlogs = view.findViewById(R.id.chipRatedBlogs);
        chipWriteBlog = view.findViewById(R.id.chipWriteBlog);
        btnSearch = view.findViewById(R.id.btnSearch);
        etSearch = view.findViewById(R.id.etSearch);
        chipWriteBlog.setOnClickListener(v -> {
            Intent i = new Intent(getActivity(), EditBlogAPIActivity.class);
            i.putExtra("isEdit", false);

            startActivity(i);
        });
        // Handle btnYourBlog click (to move to YourBlogFragment)
        chipYourBlogs.setOnClickListener(v -> {
            Intent i = new Intent(getActivity(), usersBlogActivity.class);
            i.putExtra("isEdit", false);

            startActivity(i);
        });
        chipRatedBlogs.setOnClickListener(v -> {

            Intent i = new Intent(getActivity(), RatedBlogsActivity.class);
            i.putExtra("isEdit", false);

            startActivity(i);
        });

        // Search Button onClick
        btnSearch.setOnClickListener(v -> {
            String query = etSearch.getText().toString().trim();
            filterBlogs(query);
        });

        fetchBlogsFromFirebase();
        return view;
    }

    private void fetchBlogsFromFirebase() {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("blogs");
        progressBar.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.GONE);
        // Attach the listener to get updates
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                blogList.clear();  // Clear previous data for a fresh list
                Log.d("CommunityFragment", "Number of blogs: " + dataSnapshot.getChildrenCount());

                // Iterate over each blog in the snapshot
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String id = snapshot.getKey();
                    String title = snapshot.child("title").getValue(String.class);
                    String content = snapshot.child("content").getValue(String.class);
                    Boolean isApproved = snapshot.child("isApproved").getValue(Boolean.class);
                    String userId = snapshot.child("userId").getValue(String.class);
                    if (isApproved != null && isApproved) {
                        Blog blog = new Blog(id, userId, title, content, isApproved);
                        blogList.add(blog);  // Add each blog to the list
                        Log.d("CommunityFragment", "Blog Title: " + title);  // Debugging log
                    }
                }

                // Update the filtered list and notify adapter
                filteredBlogList.clear();  // Clear filtered list
                filteredBlogList.addAll(blogList);  // Add all blogs to filtered list
                blogAdapter.notifyDataSetChanged();  // Notify adapter to refresh the view
                progressBar.setVisibility(View.GONE);
                recyclerView.setVisibility(View.VISIBLE);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("CommunityFragment", "Database error: " + databaseError.getMessage());
            }
        });
    }

    // Filter blogs based on search query
    private void filterBlogs(String query) {
        filteredBlogList.clear();  // Clear the filtered list
        if (query.isEmpty()) {
            // If no search query, show all blogs
            filteredBlogList.addAll(blogList);
        } else {
            for (Blog blog : blogList) {
                if (blog.getTitle().toLowerCase().contains(query.toLowerCase())) {
                    filteredBlogList.add(blog);  // Add blogs that match the query
                }
            }
        }
        blogAdapter.notifyDataSetChanged();  // Update RecyclerView with filtered blogs
    }

    @Override
    public void onBlogClick(Blog blog) {
        // Create an Intent to start BlogContentActivity
        Intent intent = new Intent(getContext(), BlogContentActivity.class);

        // Put necessary data in the Intent
        intent.putExtra("blogId", blog.getId());
        intent.putExtra("blogTitle", blog.getTitle());
        intent.putExtra("blogContent", blog.getContent());
        intent.putExtra("userId", blog.getUserId());

        // Toast.makeText(getContext(), "Inside onBlogClick", Toast.LENGTH_SHORT).show();


        try {
            // Start BlogContentActivity
            Log.d("CommunityFragment", "Attempting to start BlogContentActivity...");
            startActivity(intent);

        } catch (Exception e) {
            Log.e("CommunityFragment", "Error during activity start: " + e.getMessage());
        }
    }

    @Override
    public void onEditBlog(Blog blog) {
        //do nothing
    }

    @Override
    public void onDeleteBlog(Blog blog) {
        // do nothing
    }


}
