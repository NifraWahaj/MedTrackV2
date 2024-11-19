package com.example.medtrack;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
/*

public class YourBlogFragment extends Fragment implements BlogAdapter.OnBlogClickListener {

    private RecyclerView recyclerViewYourBlogs;
    private BlogAdapter blogAdapter;
    private List<Blog> blogList = new ArrayList<>();
    private List<Blog> filteredBlogList = new ArrayList<>();
    private Button btnAddBlog, btnSearch;
    private EditText etSearch;
    private LinearLayout linearLayoutYourBlogs;

    public YourBlogFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_your_blogs, container, false);

        recyclerViewYourBlogs = view.findViewById(R.id.recyclerViewYourBlogs);

        // Initialize the BlogAdapter and pass the OnBlogClickListener
        blogAdapter = new BlogAdapter(getContext(), filteredBlogList, this);
        recyclerViewYourBlogs.setAdapter(blogAdapter);






        fetchUserBlogsFromFirebase(); // Fetch data from Firebase



        return view;
    }

    // Fetch user's blogs from Firebase
    private void fetchUserBlogsFromFirebase() {
        String userEmail = UserUtils.getUserEmail(getContext()); // Get the logged-in user's email
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("blogs");

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                blogList.clear(); // Clear the previous data
                Log.d("YourBlogFragment", "Number of blogs: " + dataSnapshot.getChildrenCount());

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String id = snapshot.getKey();
                    String title = snapshot.child("title").getValue(String.class);
                    String content = snapshot.child("content").getValue(String.class);
                    String email = snapshot.child("email").getValue(String.class);
                    boolean isApproved = snapshot.child("isApproved").getValue(Boolean.class); // Use Boolean.class for boolean value

                    if (email != null && email.equals(userEmail)) {
                        Blog blog = new Blog(id, title, content, isApproved);
                        blogList.add(blog);
                    }
                }

                // Initially, show all user's blogs in the RecyclerView
                filteredBlogList.addAll(blogList);
                blogAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("YourBlogFragment", "Database error: " + databaseError.getMessage());
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d("YourBlogFragment", "onResume called");

        recyclerViewYourBlogs.setVisibility(View.VISIBLE);

    }

    // Filter blogs based on search query
    private void filterBlogs(String query) {
        filteredBlogList.clear(); // Clear the filtered list
        if (query.isEmpty()) {
            // If no search query, show all blogs
            filteredBlogList.addAll(blogList);
        } else {
            for (Blog blog : blogList) {
                if (blog.getTitle().toLowerCase().contains(query.toLowerCase())) {
                    filteredBlogList.add(blog); // Add blogs that match the query
                }
            }
        }
        blogAdapter.notifyDataSetChanged(); // Update RecyclerView with filtered blogs
    }

    @Override
    public void onBlogClick(Blog blog) {
        BlogContentFragment fragment = new BlogContentFragment();

        Bundle bundle = new Bundle();
        bundle.putString("blogId", blog.getId());
        bundle.putString("blogTitle", blog.getTitle());
        bundle.putString("blogContent", blog.getContent());
        fragment.setArguments(bundle);

        Toast.makeText(getContext(), "Inside onBlogClick", Toast.LENGTH_SHORT).show();
        recyclerView.setVisibility(View.GONE);
        btnAddBlog.setVisibility(View.GONE);
        etSearch.setVisibility(View.GONE);
        btnSearch.setVisibility(View.GONE);
        linearLayoutYourBlogs.setVisibility(View.GONE);

        try {
            Log.d("YourBlogFragment", "Attempting to replace fragment...");

            getParentFragmentManager().beginTransaction()
                    .replace(R.id.main_container, fragment, "YOUR_BLOG_CONTENT_FRAGMENT")
                    .addToBackStack("YOUR_BLOG_CONTENT_FRAGMENT")
                    .commit();

        } catch (Exception e) {
            Log.e("YourBlogFragment", "Error during fragment transaction: " + e.getMessage());
        }
    }
}
*/