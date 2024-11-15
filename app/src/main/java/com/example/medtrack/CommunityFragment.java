 package com.example.medtrack;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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
private Button buttonCommunity;
     public CommunityFragment() {
         // Required empty public constructor
     }
     @Override
     public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
         super.onViewCreated(view, savedInstanceState);

         // Set up back stack change listener to monitor fragment transactions
         //The OnBackStackChangedListener listens for changes in the back stack.
         //When the back stack count reaches 0, it means no other fragments are covering CommunityFragment,
         // so it restores the visibility of RecyclerView and Button.
         getParentFragmentManager().addOnBackStackChangedListener(() -> {
             if (getParentFragmentManager().getBackStackEntryCount() == 0) {
                 // No fragments in back stack, so restore visibility of RecyclerView and Button
                 recyclerView.setVisibility(View.VISIBLE);
                 buttonCommunity.setVisibility(View.VISIBLE);
             }
         });
     }

     @Override
     public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
         View view = inflater.inflate(R.layout.fragment_community, container, false);

         recyclerView = view.findViewById(R.id.recyclerViewCommunity);
         recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

         // Initialize the BlogAdapter and pass the OnBlogClickListener
         blogAdapter = new BlogAdapter(getContext(), blogList, this);
         recyclerView.setAdapter(blogAdapter);

           buttonCommunity = view.findViewById(R.id.buttonCommunity);
         buttonCommunity.setOnClickListener(v -> {
             Intent i = new Intent(getActivity(), EditBlogActivity.class);
             startActivity(i);
         });

         fetchBlogsFromFirebase();  // Fetch data from Firebase

         return view;
     }


    private void fetchBlogsFromFirebase() {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("blogs");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                blogList.clear(); // Clear the previous data

                // Log the number of children in the 'blogs' node
                Log.d("CommunityFragment", "Number of blogs: " + dataSnapshot.getChildrenCount());

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String id = snapshot.getKey();  // Get the unique key (id) for each blog

                    String title = snapshot.child("title").getValue(String.class);
                    String content = snapshot.child("content").getValue(String.class);

                    // Log the title and content for each blog
                    Log.d("CommunityFragment", "Blog Title: " + title);
                    Log.d("CommunityFragment", "Blog Content: " + content);

                    Blog blog = new Blog(id,title, content);
                    blogList.add(blog);
                }

                // Notify the adapter that data has changed
                blogAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Log database error
                Log.e("CommunityFragment", "Database error: " + databaseError.getMessage());
            }
        });
    }

     @Override
     public void onBlogClick(Blog blog) {
         // Handle the blog click and start the new fragment
         BlogContentFragment fragment = new BlogContentFragment();

         // Pass the blog data to the new fragment
         Bundle bundle = new Bundle();
         bundle.putString("blogId", blog.getId());  // Use "blogId" instead of "blogTitle" or "blogContent"

         bundle.putString("blogTitle", blog.getTitle());
         bundle.putString("blogContent", blog.getContent());
         fragment.setArguments(bundle);
         Toast.makeText(getContext(),"Inside onblogclick ",Toast.LENGTH_SHORT).show();
        recyclerView.setVisibility(View.GONE);
         buttonCommunity.setVisibility(View.GONE);
         // Start the new fragment transaction
          try {
             Log.d("CommunityFragment", "Attempting to replace fragment...");
             getParentFragmentManager().beginTransaction()
                     .replace(R.id.main_container, fragment)
                     .addToBackStack(null)
                     .commit();
         } catch (Exception e) {
             Log.e("CommunityFragment", "Error during fragment transaction: " + e.getMessage());
         }
     }

     @Override
     public void onResume() {
         super.onResume();

         // Check if the fragment is in the back stack and restore visibility
         if (getParentFragmentManager().getBackStackEntryCount() > 0) {
             // Handle restoring RecyclerView visibility if needed
             recyclerView.setVisibility(View.VISIBLE);
             buttonCommunity.setVisibility(View.VISIBLE);
         }
     }

     @Override
     public void onPause() {
         super.onPause();
         // Hide the RecyclerView and Button when the fragment pauses (when another fragment is displayed)
         recyclerView.setVisibility(View.GONE);
         buttonCommunity.setVisibility(View.GONE);
     }



 }
