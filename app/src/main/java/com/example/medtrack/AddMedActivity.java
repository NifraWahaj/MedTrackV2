package com.example.medtrack;

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

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class AddMedActivity extends AppCompatActivity {

    private static final String TAG = "AddMedActivity";

    private ViewPager2 viewPager;
    private MedPagerAdapter adapter;
    private List<Fragment> fragmentList = new ArrayList<>();

    // Variables to store medication details
    private String medicationName;
    private String medicationUnit;
    private String frequency;
    private String reminderTime;
    private int refillAmount;
    private int refillThreshold;

    private String medicationFrequency;  // This should be properly set by MedStep2Fragment
    private String firstIntakeDetails;
    private String secondIntakeDetails;

    // Add the missing variables
    private String startTime;  // Start time for interval medication
    private String endTime;    // End time for interval medication
    private int intervalHours; // Interval in hours between doses



    public void setIntervalDetails(String startTime, String endTime, int intervalHours) {
        this.startTime = startTime;
        this.endTime = endTime;
        this.intervalHours = intervalHours;
    }

    // Getter and Setter for medicationFrequency
    public void setMedicationFrequency(String medicationFrequency) {
        Log.d(TAG, "Setting medication frequency: " + medicationFrequency);
        this.medicationFrequency = medicationFrequency;
    }

    // Setters for intake details
    public void setFirstIntakeDetails(String time, String dosage) {
        this.firstIntakeDetails = "Time: " + time + ", Dosage: " + dosage;
    }

    public void setSecondIntakeDetails(String time, String dosage) {
        this.secondIntakeDetails = "Time: " + time + ", Dosage: " + dosage;
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
  /* private void setupFragments() {
        fragmentList.clear();
        fragmentList.add(new MedStep1Fragment());  // Step 1: Medication Name and Unit
        fragmentList.add(new MedStep2Fragment());  // Step 2: Frequency
    }*/

    // Setup fragments for the ViewPager (additions to existing method)
    private void setupFragments() {
        fragmentList.clear();
        fragmentList.add(new MedStep1Fragment());  // Step 1: Medication Name and Unit
        fragmentList.add(new MedStep2Fragment());  // Step 2: Frequency
        fragmentList.add(new MedStepMoreOptionsFragment()); // Step 3: More Options
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

    public void setMedicationUnit(String unit) {
        this.medicationUnit = unit;
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

    // Method to save medication data to Firebase
    public void saveMedicationToFirebase() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {
            Toast.makeText(this, "Error: User is not authenticated. Please log in first.", Toast.LENGTH_SHORT).show();
            return;
        }

        FirebaseDatabase database = FirebaseDatabase.getInstance("https://medtrack-68ec9-default-rtdb.asia-southeast1.firebasedatabase.app");
        DatabaseReference medsRef = database.getReference("medications");

        // Calculate time slots based on intervalHours if interval fragment is used
        List<String> intervalTimes = null;
        if (startTime != null && endTime != null && intervalHours > 0) {
            intervalTimes = calculateTimeSlots(startTime, endTime, intervalHours);
        }

        Medication medication = new Medication(
                medicationName,
                medicationUnit,
                refillAmount,
                refillThreshold,
                frequency,
                reminderTime,
                startTime,
                endTime,
                intervalHours,
                intervalTimes
        );

        String userId = currentUser.getUid();
        medsRef.child(userId).push().setValue(medication)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(AddMedActivity.this, "Medication saved successfully", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(AddMedActivity.this, "Failed to save medication", Toast.LENGTH_SHORT).show();
                });
    }



    private String scheduleOption;

    public void setScheduleOption(String scheduleOption) {
        this.scheduleOption = scheduleOption;
    }


    public void setIntervalData(String startTime, String endTime, int intervalHours) {
        // Convert startTime and endTime to hours and minutes
        int startHour = Integer.parseInt(startTime.split(":")[0]);
        int startMinute = Integer.parseInt(startTime.split(":")[1].split(" ")[0]);
        boolean isStartAm = startTime.contains("am");

        int endHour = Integer.parseInt(endTime.split(":")[0]);
        int endMinute = Integer.parseInt(endTime.split(":")[1].split(" ")[0]);
        boolean isEndAm = endTime.contains("am");

        if (!isStartAm) startHour += 12;
        if (!isEndAm) endHour += 12;

        int startInMinutes = startHour * 60 + startMinute;
        int endInMinutes = endHour * 60 + endMinute;

        if (endInMinutes <= startInMinutes) {
            // End time should be after start time
            endInMinutes += 24 * 60;
        }

        int intervalCount = (endInMinutes - startInMinutes) / (intervalHours * 60);
        // Use intervalCount for reminders
    }
    public void updateFragmentsBasedOnScheduleOption() {
        fragmentList.subList(2, fragmentList.size()).clear(); // Remove any fragments beyond step 2

        if ("Interval".equalsIgnoreCase(scheduleOption)) {
            fragmentList.add(new MedStepIntervalFragment());
        } else if ("Multiple times daily".equalsIgnoreCase(scheduleOption)) {
       //     fragmentList.add(new MedStepMultipleTimesFragment());
        } else if ("Specific days of the week".equalsIgnoreCase(scheduleOption)) {
           // fragmentList.add(new MedStepDaysOfWeekFragment());
        } else if ("Cyclic mode".equalsIgnoreCase(scheduleOption)) {
         //   fragmentList.add(new MedStepCyclicFragment());
        }

        fragmentList.add(new MedStep4Fragment()); // Add final step
        adapter.notifyDataSetChanged();
    }


    private List<String> calculateTimeSlots(String startTime, String endTime, int intervalHours) {
        // Convert startTime and endTime to Calendar objects
        Calendar startCalendar = Calendar.getInstance();
        String[] startTimeParts = startTime.split("[: ]");
        startCalendar.set(Calendar.HOUR_OF_DAY, Integer.parseInt(startTimeParts[0]));
        startCalendar.set(Calendar.MINUTE, Integer.parseInt(startTimeParts[1]));
        startCalendar.set(Calendar.AM_PM, "am".equalsIgnoreCase(startTimeParts[2]) ? Calendar.AM : Calendar.PM);

        Calendar endCalendar = Calendar.getInstance();
        String[] endTimeParts = endTime.split("[: ]");
        endCalendar.set(Calendar.HOUR_OF_DAY, Integer.parseInt(endTimeParts[0]));
        endCalendar.set(Calendar.MINUTE, Integer.parseInt(endTimeParts[1]));
        endCalendar.set(Calendar.AM_PM, "am".equalsIgnoreCase(endTimeParts[2]) ? Calendar.AM : Calendar.PM);

        List<String> timeSlots = new ArrayList<>();
        while (startCalendar.before(endCalendar)) {
            String time = String.format("%02d:%02d %s", (startCalendar.get(Calendar.HOUR) == 0) ? 12 : startCalendar.get(Calendar.HOUR),
                    startCalendar.get(Calendar.MINUTE),
                    startCalendar.get(Calendar.AM_PM) == Calendar.AM ? "am" : "pm");
            timeSlots.add(time);
            startCalendar.add(Calendar.HOUR, intervalHours);
        }

        return timeSlots;
    }


    public void goToIntervalFragment() {
        fragmentList.add(new MedStepIntervalFragment());
        goToNextStep();
    }

    public void goToMultipleTimesFragment() {
        // Assuming you have a fragment for this option, add it here
    //    fragmentList.add(new MedStepMultipleTimesFragment());
        goToNextStep();
    }

    public void goToSpecificDaysFragment() {
        // Assuming you have a fragment for this option, add it here
   //     fragmentList.add(new MedStepDaysOfWeekFragment());
        goToNextStep();
    }

    public void goToCyclicModeFragment() {
        // Assuming you have a fragment for this option, add it here
     //   fragmentList.add(new MedStepCyclicFragment());
        goToNextStep();
    }





}
