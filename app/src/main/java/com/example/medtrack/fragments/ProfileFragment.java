package com.example.medtrack.fragments;

import static android.app.Activity.RESULT_OK;

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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.medtrack.R;
import com.example.medtrack.activities.LoginActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class ProfileFragment extends Fragment {

    private static final int PICK_IMAGE = 1;
    private ImageView ivProfilePicture, ivRetrivedPicture;
    private Button btnLogout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_profile, container, false);

        ivProfilePicture = v.findViewById(R.id.ivProfilePicture);
        ivRetrivedPicture = v.findViewById(R.id.ivRetrivedPicture);
        Button btnAddPicture = v.findViewById(R.id.btnAddPicture);
        btnLogout = v.findViewById(R.id.btnLogout);  // Bind the logout button

        // Set up Add Picture button
        btnAddPicture.setOnClickListener(V -> {
            openImagePicker();
            retrieveImageFromFirebase();
        });

        // Set up Logout button functionality
        btnLogout.setOnClickListener(v1 -> logout());

        return v;
    }

    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_IMAGE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && requestCode == PICK_IMAGE && data != null) {
            Uri selectedImageUri = data.getData();

            if (selectedImageUri != null) {
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContext().getContentResolver(), selectedImageUri);
                    ivProfilePicture.setImageBitmap(bitmap);
                    storeImageInFirebase(bitmap);
                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(getContext(), "Failed to load image.", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    public String convertBitmapToBase64(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(byteArray, Base64.DEFAULT);
    }

    public void storeImageInFirebase(Bitmap bitmap) {
        String encodedImage = convertBitmapToBase64(bitmap);
        FirebaseDatabase database = FirebaseDatabase.getInstance("https://medtrack-68ec9-default-rtdb.asia-southeast1.firebasedatabase.app");
        DatabaseReference databaseRef = database.getReference("images");

        String imageId = databaseRef.push().getKey();
        if (imageId != null) {
            databaseRef.child(imageId).setValue(encodedImage)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(getContext(), "Image saved successfully!", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getContext(), "Failed to save image URL.", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    public void retrieveImageFromFirebase() {
        FirebaseDatabase database = FirebaseDatabase.getInstance("https://medtrack-68ec9-default-rtdb.asia-southeast1.firebasedatabase.app");
        DatabaseReference databaseRef = database.getReference("images");

        databaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        String base64Image = snapshot.getValue(String.class);
                        if (base64Image != null) {
                            Bitmap bitmap = convertBase64ToBitmap(base64Image);
                            ivRetrivedPicture.setImageBitmap(bitmap);
                            return;
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getContext(), "Failed to retrieve image.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public Bitmap convertBase64ToBitmap(String base64String) {
        byte[] decodedBytes = Base64.decode(base64String, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
    }

    private void logout() {
        FirebaseAuth.getInstance().signOut();  // Sign out the user from Firebase

        // Redirect to LoginActivity
        Intent intent = new Intent(getActivity(), LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);  // Clear activity stack
        startActivity(intent);

        // Show a logout success message
        Toast.makeText(getContext(), "Logged out successfully!", Toast.LENGTH_SHORT).show();

        // Finish the fragment's parent activity (optional)
        if (getActivity() != null) {
            getActivity().finish();
        }
    }
}
