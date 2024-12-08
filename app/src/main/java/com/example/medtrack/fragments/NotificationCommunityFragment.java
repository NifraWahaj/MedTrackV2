package com.example.medtrack.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.medtrack.R;
import com.example.medtrack.activities.BlogContentActivity;
import com.example.medtrack.adapters.CommunityNotificationAdapter;
import com.example.medtrack.adapters.NotificationAdapter;
import com.example.medtrack.models.Notification;
import com.example.medtrack.models.NotificationModel;
import com.example.medtrack.models.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


public class NotificationCommunityFragment extends Fragment implements CommunityNotificationAdapter.OnBlogClickListener {


    private List<Notification> notificationList = new ArrayList<>();
    private CommunityNotificationAdapter notificationAdapter;
    private ProgressBar progressBar;  // Add reference to ProgressBar

    public NotificationCommunityFragment() {
        // Required empty public constructor
    }

    public static NotificationCommunityFragment newInstance(String param1, String param2) {
        NotificationCommunityFragment fragment = new NotificationCommunityFragment();
        Bundle args = new Bundle();

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_notification_community, container, false);

        // Find RecyclerView in the inflated layout
        RecyclerView recyclerView = view.findViewById(R.id.recyclerView1);
        progressBar = view.findViewById(R.id.progressBar);  // Initialize the ProgressBar

        // Set up the adapter and RecyclerView
        notificationAdapter = new CommunityNotificationAdapter(notificationList, getContext(), this);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerView.setAdapter(notificationAdapter);

        // Fetch notifications
        fetchNotifications();

        return view;
    }

    private void fetchNotifications() {
        progressBar.setVisibility(View.VISIBLE);

        // Get the current user's ID
        String currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // Reference to the blogs collection
        DatabaseReference blogsRef = FirebaseDatabase.getInstance().getReference("blogs");

        // Query to fetch blogs where the userId matches the current user's ID
        blogsRef.orderByChild("userId").equalTo(currentUserId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                notificationList.clear();

                // Iterate through the blogs to find matching notifications
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String blogId = snapshot.getKey(); // Get the blogId

                    // Fetch the notifications for this blogId
                    fetchBlogNotifications(blogId);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(requireContext(), "Failed to load blogs", Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.GONE);  // Hide ProgressBar if failed
            }
        });
    }

    private void fetchBlogNotifications(String blogId) {
        // Reference to community notifications
        DatabaseReference notificationsRef = FirebaseDatabase.getInstance().getReference("communitynotifications");

        // Query to fetch notifications for the specific blogId
        notificationsRef.orderByChild("blogId").equalTo(blogId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String blogId = snapshot.child("blogId").getValue(String.class);
                    String message = snapshot.child("message").getValue(String.class);
                    String title = snapshot.child("title").getValue(String.class);
                    String commentText = snapshot.child("commentText").getValue(String.class);

                    long timestamp = snapshot.child("timestamp").getValue(Long.class);
                    String userId = snapshot.child("userId").getValue(String.class);
                    String username = snapshot.child("username").getValue(String.class);


                    //     public Notification(String blogId,String commenttext,String title,long timestamp,String userName) {
                    if (title.length() > 15) {
                        title = title.substring(0, 15) + "...";
                    }

                    if (commentText.length() > 15) {
                        commentText = commentText.substring(0, 15) + "...";
                    }
                    username = username + " " + message;
                    String comment = "blog Title : " + title + "\n" + "review: " + commentText;

                    //  NotificationModel notification=new NotificationModel(blogId,message,title);
                    Notification notification = new Notification(blogId, comment, title, timestamp, username);
                    if (notification != null) {
                        notificationList.add(notification);  // Add notification to the list

                    }
                }
                progressBar.setVisibility(View.GONE);  // Hide ProgressBar if failed


                notificationAdapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(requireContext(), "Failed to load notifications", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onBlogClick(String blogId) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("blogs").child(blogId);
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String title = snapshot.child("title").getValue(String.class);
                    String html = snapshot.child("content").getValue(String.class);
                    String userId = snapshot.child("userId").getValue(String.class);  // Get the userId of the author

                    // Create an Intent to start BlogContentActivity
                    Intent intent = new Intent(getContext(), BlogContentActivity.class);

                    // Put necessary data in the Intent
                    intent.putExtra("blogId", blogId);
                    intent.putExtra("blogTitle", title);
                    intent.putExtra("blogContent", html);
                    intent.putExtra("userId", userId);

                    try {
                        // Start BlogContentActivity
                        Log.d("CommunityFragment", "Attempting to start BlogContentActivity...");
                        startActivity(intent);
                    } catch (Exception e) {
                        Log.e("CommunityFragment", "Error during activity start: " + e.getMessage());
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Log.e("CommunityFragment", "Failed to read blog data: " + error.getMessage());
            }
        });
    }
}

