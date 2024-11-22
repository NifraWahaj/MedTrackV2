package com.example.medtrack.models;


import android.content.Context;
import android.content.SharedPreferences;

public class User {

    String email;
    String name;
    String userId;

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
}
