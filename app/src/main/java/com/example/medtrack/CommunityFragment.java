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
 public class CommunityFragment extends Fragment implements BlogAdapter.OnBlogClickListener {

     private RecyclerView recyclerView;
     private BlogAdapter blogAdapter;
     private List<Blog> blogList = new ArrayList<>();
     private List<Blog> filteredBlogList = new ArrayList<>();
     private Button btnCommunity, btnSearch,btnYourBlog;
     private EditText etSearch;
     private LinearLayout linearLayoutCommunity;
     public CommunityFragment() {
         // Required empty public constructor
     }

     @Override
     public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
         View view = inflater.inflate(R.layout.fragment_community, container, false);

         linearLayoutCommunity=view.findViewById(R.id.linearLayoutCommunity);
         recyclerView = view.findViewById(R.id.recyclerViewCommunity);
         recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

         // Initialize the BlogAdapter and pass the OnBlogClickListener
         blogAdapter = new BlogAdapter(getContext(), filteredBlogList, this);
         recyclerView.setAdapter(blogAdapter);

         btnCommunity = view.findViewById(R.id.buttonCommunity);
         btnSearch = view.findViewById(R.id.btnSearch);
         etSearch = view.findViewById(R.id.etSearch);
         btnYourBlog=view.findViewById(R.id.btnYourBlogs);
         btnCommunity.setOnClickListener(v -> {
             Intent i = new Intent(getActivity(), EditBlogActivity.class);
             i.putExtra("isEdit", false);

             startActivity(i);
         });
         // Handle btnYourBlog click (to move to YourBlogFragment)
         btnYourBlog.setOnClickListener(v -> {
              Intent i = new Intent(getActivity(), usersBlogActivity.class);
             i.putExtra("isEdit", false);

             startActivity(i);
         });

         // Search Button onClick
         btnSearch.setOnClickListener(v -> {
             String query = etSearch.getText().toString().trim();
             filterBlogs(query);
         });

         fetchBlogsFromFirebase();  // Fetch data from Firebase
         // Add OnBackStackChangedListener to handle view visibility when returning from other fragments
         getParentFragmentManager().addOnBackStackChangedListener(() -> {
             if (getParentFragmentManager().getBackStackEntryCount() == 0) {
                 // No fragments in back stack, so restore visibility of RecyclerView and Button
                 recyclerView.setVisibility(View.VISIBLE);
                 btnCommunity.setVisibility(View.VISIBLE);
                 etSearch.setVisibility(View.VISIBLE);
                 btnSearch.setVisibility(View.VISIBLE);
                 linearLayoutCommunity.setVisibility(View.VISIBLE);
                 btnYourBlog.setVisibility(View.VISIBLE);

             }
         });
         return view;
     }

     // Fetch blogs from Firebase
     private void fetchBlogsFromFirebase() {
         DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("blogs");
         databaseReference.addValueEventListener(new ValueEventListener() {
             @Override
             public void onDataChange(DataSnapshot dataSnapshot) {
                 blogList.clear(); // Clear the previous data
                 Log.d("CommunityFragment", "Number of blogs: " + dataSnapshot.getChildrenCount());

                 for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                     String id = snapshot.getKey();
                     String title = snapshot.child("title").getValue(String.class);
                     String content = snapshot.child("content").getValue(String.class);
                     boolean isApproved = snapshot.child("isApproved").getValue(Boolean.class); // Use Boolean.class for a boolean value
                     Log.d("CommunityFragment", "Blog Title: " + title);
                     Log.d("CommunityFragment", "Blog Content: " + content);
                    if(isApproved==true) {
                        Blog blog = new Blog(id, title, content, isApproved);
                        blogList.add(blog);
                    }
                 }

                 // Initially, show all blogs in the RecyclerView
                 filteredBlogList.addAll(blogList);
                 blogAdapter.notifyDataSetChanged();
             }

             @Override
             public void onCancelled(DatabaseError databaseError) {
                 Log.e("CommunityFragment", "Database error: " + databaseError.getMessage());
             }
         });
     }
     @Override
     public void onResume() {
         super.onResume();
         Log.d("BlogContentFragment", "onResume called");

         recyclerView.setVisibility(View.VISIBLE);
         btnCommunity.setVisibility(View.VISIBLE);
         etSearch.setVisibility(View.VISIBLE);
         btnSearch.setVisibility(View.VISIBLE);
         linearLayoutCommunity.setVisibility(View.VISIBLE);
         btnYourBlog.setVisibility(View.VISIBLE);


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
         BlogContentFragment fragment = new BlogContentFragment();

         Bundle bundle = new Bundle();
         bundle.putString("blogId", blog.getId());
         bundle.putString("blogTitle", blog.getTitle());
         bundle.putString("blogContent", blog.getContent());
         fragment.setArguments(bundle);

         Toast.makeText(getContext(), "Inside onBlogClick", Toast.LENGTH_SHORT).show();
         recyclerView.setVisibility(View.GONE);
         btnCommunity.setVisibility(View.GONE);
         etSearch.setVisibility(View.GONE);
         btnSearch.setVisibility(View.GONE);
         linearLayoutCommunity.setVisibility(View.GONE);
         btnYourBlog.setVisibility(View.GONE);

         try {
             Log.d("CommunityFragment", "Attempting to replace fragment...");


             getParentFragmentManager().beginTransaction()
                     .replace(R.id.main_container, fragment, "BLOG_CONTENT_FRAGMENT")
                     .addToBackStack("BLOG_CONTENT_FRAGMENT")
                     .commit();

         } catch (Exception e) {
             Log.e("CommunityFragment", "Error during fragment transaction: " + e.getMessage());
         }
     }




 }
