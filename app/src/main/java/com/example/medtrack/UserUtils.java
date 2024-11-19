package com.example.medtrack;


import android.content.Context;
import android.content.SharedPreferences;

public class UserUtils {

    // Method to get user data from SharedPreferences
    public static String getUserName(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("user_pref", Context.MODE_PRIVATE);
        return sharedPreferences.getString("name", ""); // Returns default empty string if name doesn't exist
    }

    public static String getUserEmail(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("user_pref", Context.MODE_PRIVATE);
        return sharedPreferences.getString("email", ""); // Returns default empty string if email doesn't exist
    }
}
