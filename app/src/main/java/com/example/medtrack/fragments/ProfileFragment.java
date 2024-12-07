package com.example.medtrack.fragments;

import static android.app.Activity.RESULT_OK;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.text.InputType;
import android.text.format.DateFormat;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.medtrack.R;
import com.example.medtrack.activities.ChangePassword;
import com.example.medtrack.activities.LoginActivity;
import com.example.medtrack.models.Medication;
import com.example.medtrack.models.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ProfileFragment extends Fragment {

    private static final int PICK_IMAGE = 1;

    private ImageButton editButton;
    private ImageView profilePicture;
    private Button btnAddPicture;
    private TextView profileName, profileEmail, registrationInfo;
    private LinearLayout LL_Report, LL_CPassword, LL_About, LL_Terms, LL_Logout;
    private DatabaseReference userRef;
    private List<Medication> medicationList = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_profile, container, false);

        // Hook up each view using findViewById
        editButton = v.findViewById(R.id.editButton);
        profilePicture = v.findViewById(R.id.profilePicture);
        btnAddPicture = v.findViewById(R.id.btnAddPicture);
        profileName = v.findViewById(R.id.profileName);
        profileEmail = v.findViewById(R.id.profileEmail);
        registrationInfo = v.findViewById(R.id.registrationInfo);
        LL_Report = v.findViewById(R.id.LL_Report);
        LL_CPassword = v.findViewById(R.id.LL_CPassword);
        LL_About = v.findViewById(R.id.LL_About);
        LL_Terms = v.findViewById(R.id.LL_Terms);
        LL_Logout = v.findViewById(R.id.LL_Logout);


        profileName.setText(User.getCurrentUserName(getContext()));
        profileEmail.setText(User.getCurrentUserName(getContext()));

        // Set up event listeners
        btnAddPicture.setOnClickListener(view -> openImagePicker());

        LL_CPassword.setOnClickListener(view -> {
            Intent intent = new Intent(getActivity(), ChangePassword.class);
            startActivity(intent);
        });

        LL_Report.setOnClickListener(view -> GetReport());

        LL_Logout.setOnClickListener(view -> logout());
        LL_About.setOnClickListener(v1 -> showAboutUsDialog());
        LL_Terms.setOnClickListener(v1 -> showTermsDialog());

        editButton.setOnClickListener(view -> showEditNameDialog());

        // Fetch and set user information from Realtime Database
        fetchUserInfoFromDatabase();

        return v;
    }
    private void GetReport(){
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {
            Toast.makeText(getActivity(), "User not logged in", Toast.LENGTH_SHORT).show();
            return;
        }

        String userId = currentUser.getUid();

        DatabaseReference medsRef = FirebaseDatabase.getInstance()
                .getReference("medications")
                .child(userId);

        medsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                medicationList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Medication medication = snapshot.getValue(Medication.class);
                    if (medication != null) {
                        medicationList.add(medication);
                    }
                }

                // After retrieving data, create PDF report
                generatePDFReport(medicationList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("Firebase", "Failed to retrieve data: " + databaseError.getMessage());
            }
        });
    }
    public void generatePDFReport(List<Medication> medicationList) {
        try {
            // Get the context and external files directory
            Context context = getContext();
            if (context != null) {
                // Create the file for the PDF
                File pdfFile = new File(context.getExternalFilesDir(null), "medication_report.pdf");

                // Create a PdfWriter instance for the PDF file
                PdfWriter writer = new PdfWriter(new FileOutputStream(pdfFile));

                // Create a PdfDocument instance
                PdfDocument pdfDoc = new PdfDocument(writer);

                // Create a Document instance with the PdfDocument and page size (optional)
                Document document = new Document(pdfDoc, PageSize.A4);

                // Add content to the document
                document.add(new Paragraph("Medication Report").setFontSize(18).setBold());

                // Add a new line for spacing
                document.add(new Paragraph("\n"));

                // Iterate over the medication list and add details
                for (Medication medication : medicationList) {
                    document.add(new Paragraph("Medication Name: " + medication.getName()));
                    document.add(new Paragraph("Frequency: " + medication.getFrequency()));
                    document.add(new Paragraph("Reminder Time: " + medication.getReminderTime()));
                    if ("Twice Daily".equals(medication.getFrequency())) {
                        document.add(new Paragraph("First Intake Details: " + medication.getFirstIntakeDetails()));
                        document.add(new Paragraph("Second Intake Details: " + medication.getSecondIntakeDetails()));
                    }
                    if (medication.getSelectedDays() != null && !medication.getSelectedDays().isEmpty()) {
                        document.add(new Paragraph("Selected Days: " + medication.getSelectedDays()));
                    }
                    document.add(new Paragraph("Start Date: " + medication.getStartDate()));
                    document.add(new Paragraph("End Date: " + medication.getEndDate()));
                    document.add(new Paragraph("Refill Amount: " + medication.getRefillAmount()));
                    document.add(new Paragraph("Refill Threshold: " + medication.getRefillThreshold()));
                    document.add(new Paragraph("\n"));
                }

                // Close the document
                document.close();

                // Log success or notify the user
                Log.d("PDF", "Medication report generated successfully.");
            } else {
                Log.e("Fragment", "Context is null, cannot generate PDF.");
            }
        } catch (IOException e) {
            e.printStackTrace();
            Log.e("PDF", "Error generating report: " + e.getMessage());
        }
    }
    private String sanitizeString(String input) {
        if (input == null) {
            return "N/A"; // Return a default value if null
        }
        return input.replaceAll("[^\\x20-\\x7e]", "");  // Remove non-ASCII characters
    }


    private void fetchUserInfoFromDatabase() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        if (currentUser != null) {
            String email = currentUser.getEmail();
            if (email == null || email.isEmpty()) {
                profileName.setText("Unknown User");
                profileEmail.setText("Email Unavailable");
                return;
            }

            userRef = FirebaseDatabase.getInstance()
                    .getReference("users");

            // Query the database to find the user with the given email
            userRef.orderByChild("email").equalTo(email)
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()) {
                                for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                                    String name = userSnapshot.child("name").getValue(String.class);
                                    String userEmail = userSnapshot.child("email").getValue(String.class);


//                                    String userimage = userSnapshot.child("image").getValue(String.class);
//
//                                    if (name != null) {
//                                        profileName.setText(name);
//                                    }
//                                    if (userEmail != null) {
//                                        profileEmail.setText(userEmail);
//                                    }
//                                    // Decode and set the image if it exists
//                                    if (userimage != "") {
//                                        Log.d("Firebase", "Base64 Image String: " + userimage);
//                                        Bitmap decodedImage = decodeBase64ToBitmap(userimage);
//                                        if (decodedImage != null) {
//                                            Log.d("Firebase", "Bitmap decoded successfully.");
//
//                                            // Update the ImageView on the main thread
//                                            getActivity().runOnUiThread(() -> profilePicture.setImageBitmap(decodedImage));
//                                        } else {
//                                            Log.e("Firebase", "Bitmap decoding failed.");
//                                            profilePicture.setImageResource(R.drawable.boy);
//                                        }
//
//                                    } else {
//                                        // Set a placeholder image if no image is found
                                    profilePicture.setImageResource(R.drawable.boy);
//                                    }

                                    userRef = userSnapshot.getRef(); // Save reference to update the name later

                                    // Set user registration date from FirebaseAuth metadata
                                    long creationTimestamp = currentUser.getMetadata().getCreationTimestamp();
                                    Date registrationDate = new Date(creationTimestamp);
                                    String formattedDate = DateFormat.format("MMMM d, yyyy", registrationDate).toString();
                                    registrationInfo.setText("Registered Since " + formattedDate);
                                }
                            } else {
                                profileName.setText("Unknown User");
                                profileEmail.setText("Email Unavailable");
                                registrationInfo.setText("Registration Date Unavailable");
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            Toast.makeText(getContext(), "Failed to load user information.", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    // Method to show the "Edit Name" dialog
    private void showEditNameDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Edit Name");

        // Set up the input
        final EditText input = new EditText(getContext());
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        input.setText(profileName.getText().toString());

        builder.setView(input);

        // Set up the buttons
        builder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String newName = input.getText().toString().trim();
                if (!newName.isEmpty() && userRef != null) {
                    userRef.child("name").setValue(newName)
                            .addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    profileName.setText(newName);
                                    Toast.makeText(getContext(), "Name updated successfully!", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(getContext(), "Failed to update name.", Toast.LENGTH_SHORT).show();
                                }
                            });
                } else {
                    Toast.makeText(getContext(), "Name cannot be empty.", Toast.LENGTH_SHORT).show();
                }
            }
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());

        builder.show();
    }

    // Method to show the "About Us" dialog
    private void showAboutUsDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("About Us");

        String aboutUsHtml = "<h2>Welcome to MedTrack!</h2>" +
                "<p>MedTrack is a comprehensive and user-friendly medication management application designed to empower users to take control of their health journey. Our mission is to simplify the way you manage your medications and wellness routines, providing a seamless experience that helps you stay on top of your health.</p>" +
                "<h3>Key Features of MedTrack:</h3>" +
                "<ul>" +
                "<li><b>Medication Reminders:</b> Set personalized reminders for your medications, whether it's a one-time dose, daily, weekly, or at specific intervals. Never miss a dose again.</li>" +
                "<li><b>Inventory Tracking:</b> Keep track of your medication inventory and receive alerts when it's time to refill. Stay ahead of your prescriptions without the worry of running out.</li>" +
                "<li><b>Community Forums:</b> MedTrack includes a community section where users can connect, share experiences, and support each other. Browse or participate in blog-style discussions related to health, medications, and wellness tips.</li>" +
                "<li><b>Health Records:</b> Maintain a digital record of your prescribed medications, including dosage instructions, start and end dates, and frequency. Keep all your health information organized and easily accessible.</li>" +
                "<li><b>Customizable Notifications:</b> Tailor your notifications to suit your schedule, ensuring the app fits seamlessly into your daily routine.</li>" +
                "</ul>" +
                "<h3>Intellectual Property & Trademark</h3>" +
                "<p>MedTrack and its logo are trademarks of MedTrack Inc. All content within the app, including text, graphics, and design, is the intellectual property of MedTrack Inc. Unauthorized use or distribution is strictly prohibited.</p>" +
                "<h3>Privacy and Security</h3>" +
                "<p>We value your privacy and are committed to protecting your personal information. All data is securely stored and handled in compliance with applicable privacy regulations. Your health data is yours, and we ensure it remains private and secure.</p>" +
                "<p>Thank you for choosing MedTrack to be your health companion. Together, we can make managing your health simpler and more effective.</p>" +
                "<p>For any queries, support, or feedback, please contact us at <a href=\"mailto:support@medtrack.com\">support@medtrack.com</a>.</p>";

        builder.setMessage(android.text.Html.fromHtml(aboutUsHtml));
        builder.setPositiveButton("OK", (dialog, which) -> dialog.dismiss());

        AlertDialog aboutDialog = builder.create();
        aboutDialog.show();
    }

    // Method to show the "Terms and Conditions" dialog
    private void showTermsDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Terms and Conditions");

        String termsHtml = "<h2>Terms and Conditions</h2>" +
                "<p>By using MedTrack, you agree to the following terms and conditions:</p>" +
                "<ol>" +
                "<li><b>Usage:</b> MedTrack is intended for personal health management and should not be used as a substitute for professional medical advice. Always consult a healthcare professional for any medical concerns.</li>" +
                "<li><b>User Responsibility:</b> You are responsible for providing accurate information when using the app. MedTrack is not liable for any issues arising from incorrect or incomplete data.</li>" +
                "<li><b>Account Security:</b> You are responsible for maintaining the confidentiality of your account credentials and ensuring that only you have access to your health information.</li>" +
                "<li><b>Data Storage:</b> MedTrack uses secure servers to store your data. However, we cannot guarantee absolute security in the transmission of data over the internet.</li>" +
                "<li><b>Third-Party Services:</b> MedTrack may contain links to third-party websites or services. We are not responsible for the content or practices of these third-party entities.</li>" +
                "</ol>" +
                "<p>Thank you for choosing MedTrack. For any questions, please contact us at <a href=\"mailto:support@medtrack.com\">support@medtrack.com</a>.</p>";

        builder.setMessage(android.text.Html.fromHtml(termsHtml));
        builder.setPositiveButton("OK", (dialog, which) -> dialog.dismiss());

        AlertDialog termsDialog = builder.create();
        termsDialog.show();
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
                    profilePicture.setImageBitmap(bitmap);
                    storeImageInFirebase(bitmap);
                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(getContext(), "Failed to load image.", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    public void storeImageInFirebase(Bitmap bitmap) {
        // Convert the Bitmap to a Base64 string
        String encodedImage = convertBitmapToBase64(bitmap);

        Log.d("Firebase", "Encoded Image: " + encodedImage);  // Log the encoded image

        // Decode and set the image
        Bitmap decodedImage = decodeBase64ToBitmap(encodedImage);
        Log.d("Firebase", "decoded Image: " + encodedImage);  // Log the encoded image

        if (decodedImage != null) {
            // Apply the decoded image to ImageView
            profilePicture.setImageBitmap(decodedImage);
        } else {
            // Handle the error when decoding fails
            Log.e("DecodeError", "Failed to decode the Base64 image.");
        }

        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = auth.getCurrentUser();

        if (currentUser != null) {
            String userId = currentUser.getUid(); // Get the unique user ID
            Log.d("Firebase", "Current user ID: " + userId);
        } else {
            Log.e("Firebase", "No user is currently logged in.");
            return;
        }
        // Reference the Firebase Realtime Database
        FirebaseDatabase database = FirebaseDatabase.getInstance("https://medtrack-68ec9-default-rtdb.asia-southeast1.firebasedatabase.app");
        DatabaseReference userRef = database.getReference("profileImages").child(currentUser.getUid()); // Use the current user's ID
        Log.d("Firebase", "Encoded Image: " + encodedImage);
        // Update the "image" field in the user's node
        userRef.child("image").setValue(encodedImage).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Log.d("Firebase", "Image updated successfully.");
                Toast.makeText(requireContext(), "Image updated successfully.", Toast.LENGTH_SHORT).show();
            } else {
                Log.e("Firebase", "Failed to update image: " + task.getException().getMessage());
                Toast.makeText(requireContext(), "Failed to update image.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public String convertBitmapToBase64(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(byteArray, Base64.DEFAULT);
    }

    private void logout() {
        FirebaseAuth.getInstance().signOut();  // Sign out the user from Firebase

        // Redirect to LoginActivity
        Intent intent = new Intent(getActivity(), LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);

        // Show a logout success message
        Toast.makeText(getContext(), "Logged out successfully!", Toast.LENGTH_SHORT).show();

        // Finish the fragment's parent activity (optional)
        if (getActivity() != null) {
            getActivity().finish();
        }
    }
    // Decode Base64 string into Bitmap
    public Bitmap decodeBase64ToBitmap(String base64Image) {
        try {
            // Decode the Base64 string into a byte array
            byte[] decodedString = Base64.decode(base64Image, Base64.DEFAULT);

            // Decode the byte array into a Bitmap
            return BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
        } catch (IllegalArgumentException e) {
            // Handle the error if Base64 string is invalid
            e.printStackTrace();
            return null;
        } catch (OutOfMemoryError e) {
            // Handle the error if the image is too large
            e.printStackTrace();
            return null;
        }
    }

}