package com.example.medtrack.activities;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;
import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

import com.example.medtrack.fragments.MedStep1Fragment;
import com.example.medtrack.fragments.MedStep2Fragment;
import com.example.medtrack.fragments.MedStep3Fragment;
import com.example.medtrack.fragments.MedStep3IntervalFragment;
import com.example.medtrack.fragments.MedStep3SpecificDaysFragment;
import com.example.medtrack.fragments.MedStep3TwoDosesFragment;
import com.example.medtrack.fragments.MedStep4Fragment;
import com.example.medtrack.R;
import com.example.medtrack.models.Medication;
import com.example.medtrack.utils.NotificationWorker;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.TimeUnit;

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
    private String startDate;
    private String endDate;
    private String medicationFrequency;
    private String firstIntakeDetails;
    private String secondIntakeDetails;
    private List<String> selectedDays = new ArrayList<>();

    // Setter methods for start and end date
    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

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
        } else if ("Interval (e.g., every X hours)".equalsIgnoreCase(medicationFrequency)) {
            Log.d(TAG, "Adding MedStep3IntervalFragment for interval frequency");
            fragmentList.add(new MedStep3IntervalFragment());  // Step 3 for Interval Doses
        } else if ("Specific days (e.g., Mon, Wed, Fri)".equalsIgnoreCase(medicationFrequency)) {
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
            medication = new Medication(
                    medicationName,
                    medicationFrequency,
                    firstIntakeDetails,
                    secondIntakeDetails,
                    refillAmount,
                    refillThreshold,
                    startDate,
                    endDate
            );
        } else if ("Specific days (e.g., Mon, Wed, Fri)".equalsIgnoreCase(medicationFrequency)) {
            String selectedDaysString = String.join(", ", selectedDays);
            medication = new Medication(
                    medicationName,
                    medicationFrequency,
                    reminderTime,
                    selectedDaysString,
                    refillAmount,
                    refillThreshold,
                    true,
                    startDate,
                    endDate
            );
        } else {
            medication = new Medication(
                    medicationName,
                    medicationFrequency,
                    reminderTime,
                    refillAmount,
                    refillThreshold,
                    startDate,
                    endDate
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
    private void scheduleMedicationNotification(Medication medication) {
        switch (medication.getFrequency()) {
            case "Once daily":
                scheduleOnceDaily(medication);
                break;
            case "Twice daily":
                scheduleTwiceDaily(medication);
                break;
            case "Interval (e.g., every X hours)":
                scheduleIntervalBased(medication);
                break;
            case "Specific days (e.g., Mon, Wed, Fri)":
                scheduleSpecificDays(medication);
                break;
        }
    }

    private void scheduleOnceDaily(Medication medication) {
        String[] timeSplit = medication.getReminderTime().split(",")[0].split(":");
        int hour = Integer.parseInt(timeSplit[0]);
        int minute = Integer.parseInt(timeSplit[1]);
        scheduleNotification(medication, hour, minute);
    }

    private void scheduleTwiceDaily(Medication medication) {
        scheduleNotificationFromDetails(medication, medication.getFirstIntakeDetails());
        scheduleNotificationFromDetails(medication, medication.getSecondIntakeDetails());
    }

    private void scheduleIntervalBased(Medication medication) {
        String[] timeParts = medication.getReminderTime().split(", ");
        int interval = Integer.parseInt(timeParts[0].split(": ")[1].replace("hours", "").trim());
        String startTime = timeParts[1].split(": ")[1].trim();
        String endTime = timeParts[2].split(": ")[1].trim();

        Calendar startCal = parseTime(startTime);
        Calendar endCal = parseTime(endTime);

        while (startCal.before(endCal)) {
            scheduleNotification(medication, startCal.get(Calendar.HOUR_OF_DAY), startCal.get(Calendar.MINUTE));
            startCal.add(Calendar.HOUR_OF_DAY, interval);
        }
    }

    private void scheduleSpecificDays(Medication medication) {
        String[] timeSplit = medication.getReminderTime().split(",")[0].split(":");
        int hour = Integer.parseInt(timeSplit[0]);
        int minute = Integer.parseInt(timeSplit[1]);
        String[] days = medication.getSelectedDays().split(", ");

        for (String day : days) {
            Calendar calendar = convertDayToCalendar(day);
            if (calendar != null) {
                scheduleNotification(medication, hour, minute, calendar.getTimeInMillis());
            }
        }
    }

    private void scheduleNotificationFromDetails(Medication medication, String details) {
        String[] timeSplit = details.split(",")[0].split(":");
        int hour = Integer.parseInt(timeSplit[0]);
        int minute = Integer.parseInt(timeSplit[1]);
        scheduleNotification(medication, hour, minute);
    }

    private void scheduleNotification(Medication medication, int hour, int minute) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, 0);

        if (calendar.before(Calendar.getInstance())) {
            calendar.add(Calendar.DAY_OF_MONTH, 1);
        }

        scheduleNotification(medication, calendar.getTimeInMillis());
    }

    private void scheduleNotification(Medication medication, int hour, int minute, long timestamp) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(timestamp);
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);
        scheduleNotification(medication, calendar.getTimeInMillis());
    }

    private void scheduleNotification(Medication medication, long triggerTimeMillis) {
        Data data = new Data.Builder()
                .putString("medName", medication.getName())
                .build();

        long delay = triggerTimeMillis - System.currentTimeMillis();

        OneTimeWorkRequest workRequest = new OneTimeWorkRequest.Builder(NotificationWorker.class)
                .setInitialDelay(delay, TimeUnit.MILLISECONDS)
                .setInputData(data)
                .build();

        WorkManager.getInstance(this).enqueue(workRequest);
    }

    private Calendar parseTime(String time) {
        String[] timeSplit = time.split(":");
        int hour = Integer.parseInt(timeSplit[0]);
        int minute = Integer.parseInt(timeSplit[1]);
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);
        return calendar;
    }

    private Calendar convertDayToCalendar(String day) {
        Calendar calendar = Calendar.getInstance();
        int today = calendar.get(Calendar.DAY_OF_WEEK);
        int targetDay = getDayConstant(day);

        if (targetDay != -1) {
            int difference = (targetDay - today + 7) % 7;
            calendar.add(Calendar.DAY_OF_MONTH, difference);
            return calendar;
        }

        return null;
    }

    private int getDayConstant(String day) {
        switch (day.toLowerCase()) {
            case "mon":
                return Calendar.MONDAY;
            case "tue":
                return Calendar.TUESDAY;
            case "wed":
                return Calendar.WEDNESDAY;
            case "thu":
                return Calendar.THURSDAY;
            case "fri":
                return Calendar.FRIDAY;
            case "sat":
                return Calendar.SATURDAY;
            case "sun":
                return Calendar.SUNDAY;
            default:
                return -1;
        }
    }



}
