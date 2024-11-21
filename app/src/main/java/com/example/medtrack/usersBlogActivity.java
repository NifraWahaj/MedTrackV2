
package com.example.medtrack;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class usersBlogActivity extends Activity implements BlogAdapter.OnBlogClickListener {

    private RecyclerView recyclerViewYourBlogs;
    private BlogAdapter blogAdapter;
    private List<Blog> blogList = new ArrayList<>();
    private Button btnAddBlog, btnSearch;
    private EditText etSearch;
    private LinearLayout linearLayoutYourBlogs;
    ImageButton btnGoBack;
   FloatingActionButton fabWriteBlog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users_blog); // Replace with your activity layout
        btnGoBack = findViewById(R.id.backButton);
        fabWriteBlog=findViewById(R.id.fabWriteBlog);
        recyclerViewYourBlogs = findViewById(R.id.recyclerViewYourBlogs);
        recyclerViewYourBlogs.setLayoutManager(new LinearLayoutManager(this));

        // Initialize the BlogAdapter and pass the OnBlogClickListener
        blogAdapter = new BlogAdapter(this, blogList, this);
        recyclerViewYourBlogs.setAdapter(blogAdapter);
        btnGoBack.setOnClickListener(v ->{

            finish();
        }  );
        fabWriteBlog.setOnClickListener(v -> {
            // For example, open a new activity (AddBlogActivity)
            Intent i = new Intent(usersBlogActivity.this, EditBlogActivity.class);
            i.putExtra("isEdit", false);

            startActivity(i);
        });

        // Fetch blogs from Firebase
        fetchUserBlogsFromFirebase();
    }

    // Fetch user's blogs from Firebase
    private void fetchUserBlogsFromFirebase() {
        String userEmail = User.getCurrentUserEmail(this); // Get the logged-in user's email
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("blogs");

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                blogList.clear(); // Clear previous data
                Log.d("YourBlogActivity", "Number of blogs: " + dataSnapshot.getChildrenCount());

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String id = snapshot.getKey();
                    String title = snapshot.child("title").getValue(String.class);
                    String content = snapshot.child("content").getValue(String.class);
                    String email = snapshot.child("userEmail").getValue(String.class);
                    boolean isApproved = snapshot.child("isApproved").getValue(Boolean.class); // Use Boolean.class for boolean value
                    Toast.makeText(usersBlogActivity.this, "EMAIL FETCHED ARE: " + email, Toast.LENGTH_SHORT).show();

                    if (email != null && email.equals(userEmail) &&isApproved==true) {
                        Blog blog = new Blog(id, title, content, isApproved);
                        blogList.add(blog);
                    }
                }
                Toast.makeText(usersBlogActivity.this, "BLOGS FETCHED: " + blogList.size(), Toast.LENGTH_SHORT).show();
                blogAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("YourBlogActivity", "Database error: " + databaseError.getMessage());
            }
        });
    }

    @Override
    public void onBlogClick(Blog blog) {
        try {
            Log.d("YourBlogActivity", "Attempting to open EditBlogActivity...");

            // Create an Intent to open EditBlogActivity
            Intent intent = new Intent(usersBlogActivity.this, EditBlogActivity.class);
            // Pass data to the activity
            intent.putExtra("blogId", blog.getId());
            intent.putExtra("blogTitle", blog.getTitle());
            intent.putExtra("blogContent", blog.getContent());
            intent.putExtra("isEdit", true);

            startActivity(intent);

        } catch (Exception e) {
            Log.e("YourBlogActivity", "Error during activity launch: " + e.getMessage());
        }
    }
}
