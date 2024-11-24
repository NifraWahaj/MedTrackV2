package com.example.medtrack.fragments;
import com.example.medtrack.activities.*; // Ensure you import the correct package

import static android.app.Activity.RESULT_OK;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.medtrack.R;
import com.example.medtrack.activities.LoginActivity;
import com.google.firebase.auth.AdditionalUserInfo;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
public class ProfileFragment extends Fragment {

    // Declare variables for views
    private View profileHeader;
    private ImageButton backButton, editButton;
    private ImageView profilePicture;
    private TextView profileName, profileEmail, registrationInfo;
    private LinearLayout menuList, LL_Report, LL_CPassword, LL_About, LL_Terms, LL_Logout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_profile, container, false);

        // Hook up each view using findViewById
        profileHeader = v.findViewById(R.id.profileHeader);
        backButton = v.findViewById(R.id.backButton);
        editButton = v.findViewById(R.id.editButton);
        profilePicture = v.findViewById(R.id.profilePicture);
        profileName = v.findViewById(R.id.profileName);
        profileEmail = v.findViewById(R.id.profileEmail);
        registrationInfo = v.findViewById(R.id.registrationInfo);
        menuList = v.findViewById(R.id.menuList);
        LL_Report = v.findViewById(R.id.LL_Report);
        LL_CPassword = v.findViewById(R.id.LL_CPassword);
        LL_About = v.findViewById(R.id.LL_About);
        LL_Terms = v.findViewById(R.id.LL_Terms);
        LL_Logout = v.findViewById(R.id.LL_Logout);

        // Set up event listeners for the buttons or other interactive views
        backButton.setOnClickListener(v1 -> {
            // Handle back button click
        });

        editButton.setOnClickListener(v1 -> {
            // Handle edit button click
        });

        LL_Report.setOnClickListener(v1 -> {
            // Handle Report item click
        });

        LL_CPassword.setOnClickListener(v1 -> {

            Log.d("1","1");
            Intent intent = new Intent(getActivity(), ChangePassword.class);
            startActivity(intent);

        });

        LL_About.setOnClickListener(v1 -> {
            // Handle About Us item click
        });

        LL_Terms.setOnClickListener(v1 -> {
            // Handle Terms and Conditions item click
        });

        LL_Logout.setOnClickListener(v1 -> {
            // Handle Logout item click
        });

        return v;
    }
}
