
package com.example.medtrack.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
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
        blogAdapter = new BlogAdapter(this, blogList, this,true);
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
                    String userId= snapshot.child("userId").getValue(String.class);
                    String title = snapshot.child("title").getValue(String.class);
                    String content = snapshot.child("content").getValue(String.class);
                    String email = snapshot.child("userEmail").getValue(String.class);
                    boolean isApproved = snapshot.child("isApproved").getValue(Boolean.class); // Use Boolean.class for boolean value
                    Toast.makeText(usersBlogActivity.this, "EMAIL FETCHED ARE: " + email, Toast.LENGTH_SHORT).show();

                    if(userId!=null && userId.equals(User.getCurrentUserId(getApplicationContext()))&&isApproved==true){
                        Blog blog = new Blog(id, title, content, isApproved);
                        blogList.add(blog);
                    }
                 //   if (email != null && email.equals(userEmail) &&isApproved==true) {
                //        Blog blog = new Blog(id, title, content, isApproved);
                 //       blogList.add(blog);
                  //  }
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
        // do nothing
    }

    @Override
    public void onEditBlog(Blog blog) {
        try {
            Log.d("YourBlogActivity", "Attempting to open EditBlogActivity...");

            // Create an Intent to open EditBlogActivity
            Intent intent = new Intent(usersBlogActivity.this, EditBlogAPIActivity.class);
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

    @Override
    public void onDeleteBlog(Blog blog) {
        // Create a confirmation dialog
        new AlertDialog.Builder(usersBlogActivity.this)
                .setTitle("Delete Blog")
                .setMessage("Are you sure you want to delete this blog?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    // If the user confirms, delete the blog
                    String blogId = blog.getId();  // Get the unique ID of the blog to delete
                    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("blogs");

                    // Delete the blog from Firebase
                    databaseReference.child(blogId).removeValue()
                            .addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    Toast.makeText(usersBlogActivity.this, "Blog deleted successfully", Toast.LENGTH_SHORT).show();
                                    // Optionally, remove the blog from your local list and notify the adapter
                                    blogList.remove(blog);
                                    blogAdapter.notifyDataSetChanged();  // Refresh the adapter
                                } else {
                                    Toast.makeText(usersBlogActivity.this, "Failed to delete blog", Toast.LENGTH_SHORT).show();
                                }
                            })
                            .addOnFailureListener(e -> {
                                Log.e("YourBlogActivity", "Failed to delete blog: " + e.getMessage());
                                Toast.makeText(usersBlogActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            });
                })
                .setNegativeButton("No", (dialog, which) -> {
                    // If the user cancels, do nothing (close the dialog)
                    dialog.dismiss();
                })
                .show();  // Show the dialog
    }

}
