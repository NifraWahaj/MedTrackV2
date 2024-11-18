package com.example.medtrack;

import android.icu.text.SimpleDateFormat;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.Gson;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;


public class AddMedActivity extends AppCompatActivity {
    private static final String TAG = "AddMedActivity";

    private ViewPager2 viewPager;
    private MedPagerAdapter adapter;
    private List<Fragment> fragmentList = new ArrayList<>();

    // Variables to store medication details
    private String medicationName;
    private String frequency;
    private String reminderTime;
    private int refillAmount;
    private int refillThreshold;

    private String medicationFrequency;  // This should be properly set by MedStep2Fragment
    private String firstIntakeDetails;
    private String secondIntakeDetails;

    // List to store selected days for "Specific days of the week" option
    private List<String> selectedDays = new ArrayList<>();

    // Getter and Setter for medicationFrequency
    public void setMedicationFrequency(String medicationFrequency) {
        Log.d(TAG, "Setting medication frequency: " + medicationFrequency);
        this.medicationFrequency = medicationFrequency;
    }

    // Setter methods for intake details
    public void setFirstIntakeDetails(String time, String dosage) {
        this.firstIntakeDetails = time + ", Dosage: " + dosage;
    }

    public void setSecondIntakeDetails(String time, String dosage) {
        this.secondIntakeDetails = time + ", Dosage: " + dosage;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_med);

        // Setup ViewPager2
        viewPager = findViewById(R.id.viewPager);
        setupFragments(); // Create a list of initial fragments
        adapter = new MedPagerAdapter(this);
        viewPager.setAdapter(adapter);
    }

    // Setup fragments for the ViewPager
    private void setupFragments() {
        fragmentList.clear();
        fragmentList.add(new MedStep1Fragment());  // Step 1: Medication Name
        fragmentList.add(new MedStep2Fragment());  // Step 2: Frequency
    }

    // Update fragments based on the selected frequency
    public void updateFragmentsBasedOnFrequency() {
        if (medicationFrequency == null) {
            Log.e(TAG, "Medication frequency is null, cannot update fragments properly.");
            return;
        }

        Log.d(TAG, "Updating fragments based on frequency: " + medicationFrequency);

        // Clear existing fragments from step 3 onwards
        if (fragmentList.size() > 2) {
            fragmentList.subList(2, fragmentList.size()).clear();
        }

        // Add Step 3 based on the user's frequency choice
        if ("Twice daily".equalsIgnoreCase(medicationFrequency)) {
            Log.d(TAG, "Adding MedStep3TwoDosesFragment for twice daily frequency");
            fragmentList.add(new MedStep3TwoDosesFragment());  // Step 3 for Two Doses
        } else if ("Interval (e.g., every X hours, every X days)".equalsIgnoreCase(medicationFrequency)) {
            Log.d(TAG, "Adding MedStep3IntervalFragment for interval frequency");
            fragmentList.add(new MedStep3IntervalFragment());  // Step 3 for Interval Doses
        } else if ("Specific days of the week (e.g., Mon, Wed, Fri)".equalsIgnoreCase(medicationFrequency)) {
            Log.d(TAG, "Adding MedStep3SpecificDaysFragment for specific days frequency");
            fragmentList.add(new MedStep3SpecificDaysFragment());  // Step 3 for Specific Days
        } else {
            Log.d(TAG, "Adding MedStep3Fragment for once daily frequency");
            fragmentList.add(new MedStep3Fragment());  // Step 3 for Single Dose
        }

        // Add Step 4 (Refill Reminder Step)
        Log.d(TAG, "Adding MedStep4Fragment");
        MedStep4Fragment finalStepFragment = new MedStep4Fragment();
        finalStepFragment.setRetainInstance(true);
        fragmentList.add(finalStepFragment);

        // Notify adapter about the updated fragment list
        adapter.notifyDataSetChanged();
    }

    // Adapter for handling fragments in the ViewPager2
    private class MedPagerAdapter extends FragmentStateAdapter {
        public MedPagerAdapter(@NonNull AppCompatActivity fragmentActivity) {
            super(fragmentActivity);
        }

        @NonNull
        @Override
        public Fragment createFragment(int position) {
            Log.d(TAG, "Creating fragment for position: " + position);
            return fragmentList.get(position);
        }

        @Override
        public int getItemCount() {
            return fragmentList.size();
        }
    }

    // Method to navigate to the next step
    public void goToNextStep() {
        if (viewPager.getCurrentItem() < fragmentList.size() - 1) {
            // Go to the next fragment in the ViewPager
            viewPager.setCurrentItem(viewPager.getCurrentItem() + 1);
        }
    }

    // Setter methods for data collection
    public void setMedicationName(String name) {
        this.medicationName = name;
    }

    public void setFrequency(String frequency) {
        this.frequency = frequency;
    }

    public void setReminderTime(String time) {
        this.reminderTime = time;
    }

    public void setRefillAmount(int amount) {
        this.refillAmount = amount;
    }

    public void setRefillThreshold(int threshold) {
        this.refillThreshold = threshold;
    }

    public void setSelectedDays(List<String> days) {
        this.selectedDays = days;
    }

    public List<String> getSelectedDays() {
        return selectedDays;
    }

    // Method to save medication data to Firebase
    public void saveMedicationToFirebase() {
        // Get the current user
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {
            // User is not authenticated, handle the error
            Toast.makeText(this, "Error: User is not authenticated. Please log in first.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Create a Firebase reference
        FirebaseDatabase database = FirebaseDatabase.getInstance("https://medtrack-68ec9-default-rtdb.asia-southeast1.firebasedatabase.app");
        DatabaseReference medsRef = database.getReference("medications");

        // Create a medication object to save
        Medication medication;

        if ("Twice daily".equalsIgnoreCase(medicationFrequency)) {
            // Create medication object with twice daily details
            medication = new Medication(
                    medicationName,
                    medicationFrequency,
                    firstIntakeDetails,
                    secondIntakeDetails,
                    refillAmount,
                    refillThreshold
            );
        } else if ("Specific days of the week (e.g., Mon, Wed, Fri)".equalsIgnoreCase(medicationFrequency)) {
            // Convert selectedDays list to a single string
            String selectedDaysString = String.join(", ", selectedDays);

            // Create medication object with specific days details using the new constructor
            medication = new Medication(
                    medicationName,
                    medicationFrequency,
                    reminderTime,
                    selectedDaysString,
                    refillAmount,
                    refillThreshold,
                    true // The dummy boolean field to distinguish this constructor
            );
        } else {
            // Create medication object with single dose or interval details
            medication = new Medication(
                    medicationName,
                    medicationFrequency,
                    reminderTime,
                    refillAmount,
                    refillThreshold
            );
        }

        // Save the medication object to Firebase
        String userId = currentUser.getUid();
        Log.d(TAG, "Saving Medication with Name: " + medicationName);
        Log.d(TAG, "Frequency: " + medicationFrequency);
        Log.d(TAG, "Reminder Time: " + reminderTime);
        Log.d(TAG, "Selected Days: " + selectedDays);

        medsRef.child(userId).push().setValue(medication)
                .addOnSuccessListener(aVoid -> {
                    // Show a success message
                    Toast.makeText(AddMedActivity.this, "Medication saved successfully", Toast.LENGTH_SHORT).show();
                    finish(); // Close the activity after saving
                })
                .addOnFailureListener(e -> {
                    // Show an error message
                    Toast.makeText(AddMedActivity.this, "Failed to save medication", Toast.LENGTH_SHORT).show();
                });
    }



}
