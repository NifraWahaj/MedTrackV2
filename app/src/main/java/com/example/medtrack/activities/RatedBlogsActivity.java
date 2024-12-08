

package com.example.medtrack.activities;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.medtrack.R;
import com.example.medtrack.adapters.BlogAdapter;
import com.example.medtrack.models.Blog;
import com.example.medtrack.models.User;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class RatedBlogsActivity extends Activity implements BlogAdapter.OnBlogClickListener {

    private RecyclerView recyclerView;
    private BlogAdapter blogAdapter;
    private List<Blog> blogList = new ArrayList<>();
    private Button btnAddBlog, btnSearch;
    private ProgressBar progressBar;  // Declare ProgressBar

    ImageButton btnGoBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rated_blogs); // Replace with your activity layout
        btnGoBack = findViewById(R.id.backButton);
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        progressBar = findViewById(R.id.progressBar); // Initialize ProgressBar

        // Initialize the BlogAdapter and pass the OnBlogClickListener
        blogAdapter = new BlogAdapter(this, blogList, this, false);
        recyclerView.setAdapter(blogAdapter);
        btnGoBack.setOnClickListener(v -> {

            finish();
        });


        // Fetch blogs from Firebase
        fetchRatedBlogsFromFirebase();
    }

    // Fetch user's rated blogs from Firebase
    private void fetchRatedBlogsFromFirebase() {
        String currentUserId = User.getCurrentUserId(this); // Get the logged-in user's ID
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("blogs");
        progressBar.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.GONE);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                blogList.clear(); // Clear previous data
                Log.d("RatedBlogs", "Total blogs: " + dataSnapshot.getChildrenCount());

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String blogId = snapshot.getKey();
                    String title = snapshot.child("title").getValue(String.class);
                    String content = snapshot.child("content").getValue(String.class);
                    String userId = snapshot.child("userId").getValue(String.class);
                    Boolean isApproved = snapshot.child("isApproved").getValue(Boolean.class);

                    // Check if the blog is approved and the current user has reviewed/ rated it
                    DataSnapshot reviewAndRatingSnapshot = snapshot.child("reviews_and_ratings").child(currentUserId);

                    if (reviewAndRatingSnapshot.exists() && Boolean.TRUE.equals(isApproved)) {

                        // Create a Blog object and add it to the list
                        Blog blog = new Blog(blogId, userId, title, content, isApproved);
                        blogList.add(blog);
                        Log.d("RatedBlogs", "Blog added: " + title);
                    }
                }

                blogAdapter.notifyDataSetChanged(); // Notify adapter of data change
                progressBar.setVisibility(View.GONE);
                recyclerView.setVisibility(View.VISIBLE);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("RatedBlogs", "Database error: " + databaseError.getMessage());
            }
        });
    }

    @Override
    public void onBlogClick(Blog blog) {
        Toast.makeText(this, "blog clicked in rated", Toast.LENGTH_SHORT).show();

        // Create an Intent to start BlogContentActivity
        Intent intent = new Intent(RatedBlogsActivity.this, BlogContentActivity.class);
        // Put necessary data in the Intent
        intent.putExtra("blogId", blog.getId());
        intent.putExtra("blogTitle", blog.getTitle());
        intent.putExtra("blogContent", blog.getContent());
        intent.putExtra("userId", blog.getUserId());


        try {
            // Start BlogContentActivity
            Log.d("RATEDbLOGS", "Attempting to start BlogContentActivity...");
            startActivity(intent);

        } catch (Exception e) {
            Log.e("RATEDbLOGS", "Error during activity start: " + e.getMessage());
        }
    }


    @Override
    public void onEditBlog(Blog blog) {
        // do nothing
    }

    @Override
    public void onDeleteBlog(Blog blog) {
        // do nothing
    }


}
