package com.example.medtrack.fragments;

import static android.app.Activity.RESULT_OK;

import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import com.itextpdf.kernel.colors.Color;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.layout.borders.Border;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Table;

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


        LinearLayout LL_Delete = v.findViewById(R.id.LL_Delete);
        LL_Delete.setOnClickListener(view -> confirmAndDeleteAccount());

        profileName.setText(User.getCurrentUserName(getContext()));
        profileEmail.setText(User.getCurrentUserEmail(getContext()));

        // Set up event listeners
        btnAddPicture.setOnClickListener(view -> openImagePicker());

        LL_CPassword.setOnClickListener(view -> {
            Intent intent = new Intent(getActivity(), ChangePassword.class);
            startActivity(intent);
        });

        LL_Report.setOnClickListener(view -> {
            Log.d("ProfileFragment", "Report button clicked");
            GetReport();
        });


        LL_Logout.setOnClickListener(view -> logout());
        LL_About.setOnClickListener(v1 -> showAboutUsDialog());
        LL_Terms.setOnClickListener(v1 -> showTermsDialog());

        editButton.setOnClickListener(view -> showEditNameDialog());

        // Fetch and set user information from Realtime Database
        fetchUserInfoFromDatabase();

        return v;
    }
    private void GetReport() {
        Log.d("ProfileFragment", "GetReport() called");
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {
            Log.d("ProfileFragment", "No user logged in");
            Toast.makeText(getActivity(), "User not logged in", Toast.LENGTH_SHORT).show();
            return;
        }

        String userId = currentUser.getUid();
        Log.d("ProfileFragment", "Fetching data for user: " + userId);

        DatabaseReference medsRef = FirebaseDatabase.getInstance().getReference("medications").child(userId);
        medsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.d("Firebase", "Data snapshot received: " + dataSnapshot.toString());
                medicationList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Medication medication = snapshot.getValue(Medication.class);
                    if (medication != null) {
                        medicationList.add(medication);
                    }
                }
                Log.d("ProfileFragment", "Number of medications retrieved: " + medicationList.size());
                DatabaseReference medsRef = FirebaseDatabase.getInstance().getReference("medications").child(userId);
                medsRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            generatePDFReportFromSnapshot(dataSnapshot);
                        } else {
                            Log.d("Firebase", "No medications found for user.");
                            Toast.makeText(getContext(), "No medications to generate a report.", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Log.e("Firebase", "Failed to fetch data: " + databaseError.getMessage());
                    }
                });

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("Firebase", "Failed to retrieve data: " + databaseError.getMessage());
            }
        });
    }



    public void generatePDFReportFromSnapshot(DataSnapshot snapshot) {
        List<Medication> medicationList = new ArrayList<>();

        // Parse the DataSnapshot into a list of medications
        for (DataSnapshot medicationSnapshot : snapshot.getChildren()) {
            Medication medication = medicationSnapshot.getValue(Medication.class);
            if (medication != null) {
                medicationList.add(medication);
            }
        }

        // Now generate the report using the parsed list
        Log.d("ProfileFragment", "generatePDFReport called with " + medicationList.size() + " medications");

        try {
            Context context = getContext();
            if (context != null) {
                File pdfFile = new File(context.getExternalFilesDir(null), "medication_report.pdf");
                PdfWriter writer = new PdfWriter(new FileOutputStream(pdfFile));
                PdfDocument pdfDoc = new PdfDocument(writer);
                Document document = new Document(pdfDoc, PageSize.A4);

                // Styling
                DeviceRgb blueColor = new DeviceRgb(0, 102, 204);
                DeviceRgb grayColor = new DeviceRgb(64, 64, 64);

                // Title Section
                document.add(new Paragraph("Medication Report")
                        .setFontSize(24)
                        .setBold()
                        .setFontColor(blueColor)
                        .setTextAlignment(com.itextpdf.layout.property.TextAlignment.CENTER));
                document.add(new Paragraph("Generated by MedTrack").setFontSize(14).setTextAlignment(com.itextpdf.layout.property.TextAlignment.CENTER));
                document.add(new Paragraph("Date: " + new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(new Date()))
                        .setFontSize(12)
                        .setTextAlignment(com.itextpdf.layout.property.TextAlignment.CENTER));
                document.add(new Paragraph("\n\n")); // Spacer

                // Analytics Section
                document.add(new Paragraph("Analytics")
                        .setFontSize(16)
                        .setBold()
                        .setFontColor(blueColor));
                document.add(new Paragraph("Total Medications: " + medicationList.size()));
                document.add(new Paragraph("Most Common Frequency: " + getMostCommonFrequency(medicationList)));
                document.add(new Paragraph("Longest Active Medication: " + getLongestActiveMedication(medicationList)));
                document.add(new Paragraph("\n"));

                // Medication Table
                float[] columnWidths = {150, 100, 150, 150}; // Adjust column widths
                Table table = new Table(columnWidths);
                table.addHeaderCell(new Cell().add(new Paragraph("Medication Name").setBold().setFontColor(blueColor)));
                table.addHeaderCell(new Cell().add(new Paragraph("Frequency").setBold().setFontColor(blueColor)));
                table.addHeaderCell(new Cell().add(new Paragraph("Start Date").setBold().setFontColor(blueColor)));
                table.addHeaderCell(new Cell().add(new Paragraph("End Date").setBold().setFontColor(blueColor)));

                // Populate Table Rows
                for (Medication medication : medicationList) {
                    table.addCell(new Cell().add(new Paragraph(medication.getName())));
                    table.addCell(new Cell().add(new Paragraph(medication.getFrequency())));
                    table.addCell(new Cell().add(new Paragraph(medication.getStartDate())));
                    table.addCell(new Cell().add(new Paragraph(medication.getEndDate())));
                }

                document.add(table);

                // Footer Section
                document.add(new Paragraph("\n"));
                document.add(new Paragraph("This report is generated automatically by MedTrack.")
                        .setFontSize(10)
                        .setFontColor(grayColor)
                        .setTextAlignment(com.itextpdf.layout.property.TextAlignment.CENTER));
                document.add(new Paragraph("For inquiries, contact support@medtrack.com.")
                        .setFontSize(10)
                        .setFontColor(grayColor)
                        .setTextAlignment(com.itextpdf.layout.property.TextAlignment.CENTER));

                // Close Document
                document.close();

                Log.d("PDF", "Medication report generated successfully.");
                Uri contentUri = FileProvider.getUriForFile(context, "com.example.medtrack.fileprovider", pdfFile);
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setDataAndType(contentUri, "application/pdf");
                intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                Intent chooser = Intent.createChooser(intent, "Open Report");
                try {
                    context.startActivity(chooser);
                } catch (ActivityNotFoundException e) {
                    Toast.makeText(context, "No application found to open PDF", Toast.LENGTH_SHORT).show();
                }

            } else {
                Log.e("Fragment", "Context is null, cannot generate PDF.");
            }
        } catch (IOException e) {
            e.printStackTrace();
            Log.e("PDF", "Error generating report: " + e.getMessage());
        }
    }


    private String getMostCommonFrequency(List<Medication> medications) {
        Map<String, Integer> frequencyCount = new HashMap<>();
        for (Medication medication : medications) {
            frequencyCount.put(medication.getFrequency(), frequencyCount.getOrDefault(medication.getFrequency(), 0) + 1);
        }
        return Collections.max(frequencyCount.entrySet(), Map.Entry.comparingByValue()).getKey();
    }

    private String getLongestActiveMedication(List<Medication> medications) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        long maxDuration = 0;
        String longestMedication = "";
        for (Medication medication : medications) {
            try {
                Date startDate = dateFormat.parse(medication.getStartDate());
                Date endDate = dateFormat.parse(medication.getEndDate());
                long duration = endDate.getTime() - startDate.getTime();
                if (duration > maxDuration) {
                    maxDuration = duration;
                    longestMedication = medication.getName();
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        return longestMedication;
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
    public static Bitmap decodeBase64ToBitmap(String base64Image) {
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
    private void confirmAndDeleteAccount() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        if (currentUser == null) {
            Toast.makeText(getActivity(), "No user is logged in.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Show confirmation dialog
        new AlertDialog.Builder(getContext())
                .setTitle("Delete Account")
                .setMessage("Are you sure you want to permanently delete your account? This action cannot be undone.")
                .setPositiveButton("Delete", (dialog, which) -> {
                    // Proceed with account deletion
                    deleteAccount(currentUser);
                })
                .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
                .create()
                .show();
    }

    private void deleteAccount(FirebaseUser currentUser) {
        // Delete user-related data from Firebase Realtime Database
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users").child(currentUser.getUid());
        userRef.removeValue().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                // Remove user authentication account
                currentUser.delete().addOnCompleteListener(deleteTask -> {
                    if (deleteTask.isSuccessful()) {
                        Toast.makeText(getActivity(), "Account deleted successfully.", Toast.LENGTH_SHORT).show();

                        // Redirect to Login or Home screen
                        Intent intent = new Intent(getActivity(), LoginActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);

                        if (getActivity() != null) {
                            getActivity().finish();
                        }
                    } else {
                        Toast.makeText(getActivity(), "Failed to delete account: " + deleteTask.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                Toast.makeText(getActivity(), "Failed to delete user data: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


}