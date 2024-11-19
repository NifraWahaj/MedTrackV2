package com.example.medtrack;

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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class ProfileFragment extends Fragment {

    private static final int PICK_IMAGE = 1;
    private ImageView ivProfilePicture, ivRetrivedPicture;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_profile, container, false);

        ivProfilePicture = v.findViewById(R.id.ivProfilePicture);
        Button btnAddPicture = v.findViewById(R.id.btnAddPicture);
        ivRetrivedPicture = v.findViewById(R.id.ivRetrivedPicture);

        btnAddPicture.setOnClickListener(V -> {
            openImagePicker();
            retrieveImageFromFirebase();

        });

        // Optionally, you can retrieve an image from Firebase and display it

        return v;
    }

    private void openImagePicker() {
        // Open the image picker to select an image from the gallery
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_IMAGE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && requestCode == PICK_IMAGE && data != null) {
            // Get the URI of the selected image
            Uri selectedImageUri = data.getData();

            if (selectedImageUri != null) {
                try {
                    // Convert the URI to a Bitmap
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContext().getContentResolver(), selectedImageUri);

                    // Display the image in an ImageView
                    ivProfilePicture.setImageBitmap(bitmap);

                    // Convert the Bitmap to Base64 and upload to Firebase
                    storeImageInFirebase(bitmap);
                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(getContext(), "Failed to load image.", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    // Convert Bitmap to Base64 String
    public String convertBitmapToBase64(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        // Compress the bitmap into JPEG format, adjust the quality to 100%
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream.toByteArray();
        // Encode the byte array into a Base64 string
        return Base64.encodeToString(byteArray, Base64.DEFAULT);
    }

    // Store the Base64-encoded image in Firebase Realtime Database
    public void storeImageInFirebase(Bitmap bitmap) {
        String encodedImage = convertBitmapToBase64(bitmap);

        // Get a reference to your Firebase Realtime Database
        FirebaseDatabase database = FirebaseDatabase.getInstance("https://medtrack-68ec9-default-rtdb.asia-southeast1.firebasedatabase.app");
        DatabaseReference databaseRef = database.getReference("images");

        // Push the Base64 string into the database under a new key
        String imageId = databaseRef.push().getKey(); // Generate a unique key
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

    // Optionally, retrieve the image from Firebase and display it
    public void retrieveImageFromFirebase() {
        // Get a reference to the Firebase Realtime Database
        FirebaseDatabase database = FirebaseDatabase.getInstance("https://medtrack-68ec9-default-rtdb.asia-southeast1.firebasedatabase.app");
        DatabaseReference databaseRef = database.getReference("images");

        // Retrieve the images stored under the "images" node
        databaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // Iterate through all child nodes to retrieve the Base64 image string
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        String base64Image = snapshot.getValue(String.class);
                        if (base64Image != null) {
                            // Convert Base64 string back to Bitmap and display it
                            Bitmap bitmap = convertBase64ToBitmap(base64Image);
                            ivRetrivedPicture.setImageBitmap(bitmap);
                            return;  // Display the first retrieved image (or break if you need a specific one)
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

    // Convert Base64 string back to Bitmap
    public Bitmap convertBase64ToBitmap(String base64String) {
        byte[] decodedBytes = Base64.decode(base64String, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
    }
}
