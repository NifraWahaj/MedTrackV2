package com.example.medtrack.activities;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.viewpager2.widget.ViewPager2;

import com.example.medtrack.R;
import com.example.medtrack.adapters.ViewPagerAdapter;
import com.google.android.material.badge.BadgeDrawable;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {

    TabLayout tabLayout;
    ViewPager2 vp2;
    ViewPagerAdapter adapter;
    int count = 0;
    boolean flag = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


        adapter = new ViewPagerAdapter(this);
        vp2 = findViewById(R.id.viewpager2);
        vp2.setAdapter(adapter);
        tabLayout = findViewById(R.id.tabLayout);

        TabLayoutMediator tabLayoutMediator = new TabLayoutMediator(tabLayout, vp2, new TabLayoutMediator.TabConfigurationStrategy() {
            @Override
            public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
                switch (position) {
                    case 0:
                        tab.setText("Alerts");
                        tab.setIcon(R.drawable.ic_notifications);

                        break;
                    case 1:
                        tab.setText("Meds");
                        tab.setIcon(R.drawable.ic_pills);
                        break;
                    case 2:
                        tab.setText("Home");
                        tab.setIcon(R.drawable.ic_home);
                        break;
                    case 3:
                        tab.setText("Forums");
                        tab.setIcon(R.drawable.ic_community);
                        break;
                    case 4:
                        tab.setText("Profile");
                        tab.setIcon(R.drawable.ic_profile);
                        break;
                    default:
                        break;
                }
            }
        });
        tabLayoutMediator.attach();

        // Set the default item to Home (index 2)
        vp2.setCurrentItem(2, false);

        vp2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);


                TabLayout.Tab selectedTab = tabLayout.getTabAt(position);
                BadgeDrawable badgeDrawable = selectedTab.getBadge();
                if (badgeDrawable != null) {
                    count = 0;
                    badgeDrawable.setNumber(count);
                    if (!flag)
                        flag = true;
                    else
                        badgeDrawable.setVisible(false);
                }
            }
        });

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();
            String email = currentUser.getEmail();
           // Toast.makeText(this, "main activity email"+email, Toast.LENGTH_SHORT).show();
            // Fetch additional user details if required
            fetchUserFromDatabase(userId);
        }

    }
    private void fetchUserFromDatabase(String userId) {
        // Reference to the "users" node in Firebase Database
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users");

        // Query the database for the specific user ID
        userRef.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // Extract user details from the snapshot
                    String name = dataSnapshot.child("name").getValue(String.class);
                    String email = dataSnapshot.child("email").getValue(String.class);
                    Toast.makeText(MainActivity.this, email+name+"thiss", Toast.LENGTH_SHORT).show();
                    // Store user details in SharedPreferences
                    storeUserInSharedPreferences(name, email,userId);

                } else {
                    // Handle case where user ID does not exist
                    Toast.makeText(MainActivity.this, "User not found in the database.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle errors
                Log.e("LoginActivity", "Database error: " + databaseError.getMessage());
                Toast.makeText(MainActivity.this, "Failed to fetch user data.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void storeUserInSharedPreferences(String name, String email,String userId) {
        SharedPreferences sharedPreferences = getSharedPreferences("user_pref", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        // Clear all stored preferences
        editor.clear();
        editor.putString("name", name);
        editor.putString("userId", userId); // Store user ID

        editor.putString("email", email);
        editor.apply();
    }

}