package com.example.medtrack.models;


import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class User {

    String email;
    String name;


    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    // Method to get user data from SharedPreferences
    public static String getCurrentUserName(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("user_pref", Context.MODE_PRIVATE);
        return sharedPreferences.getString("name", ""); // Returns default empty string if name doesn't exist
    }
    public static String getCurrentUserId(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("user_pref", Context.MODE_PRIVATE);
        return sharedPreferences.getString("userId", null); // Returns default null string if id doesn't exist
    }

    public static String getCurrentUserEmail(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("user_pref", Context.MODE_PRIVATE);
        return sharedPreferences.getString("email", ""); // Returns default empty string if email doesn't exist
    }

    public static User fetchUserFromDatabase(String userId){
        User user= new User();

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
                       user.setName(name);
                      user.setEmail(email);
                        Log.d("this", name +email + user.getEmail());
                    } else {
                        // Handle case where user ID does not exist
                     }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    // Handle errors
                    Log.e("LoginActivity", "Database error: " + databaseError.getMessage());
                 }
            });
    return user;
    }



}
