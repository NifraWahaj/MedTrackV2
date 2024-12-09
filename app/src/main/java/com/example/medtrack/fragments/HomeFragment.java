package com.example.medtrack.fragments;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.medtrack.R;
import com.example.medtrack.activities.AddMedActivity;
import com.example.medtrack.adapters.CalendarAdapter;
import com.example.medtrack.adapters.MedicationAdapter;
import com.example.medtrack.models.Medication;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.Set;

public class HomeFragment extends Fragment {

    private RecyclerView calendarRecyclerView, medicationRecyclerView;
    private TextView textViewSelectedDate, textViewNoMedications;
    private LinearLayoutManager layoutManager;
    private List<Medication> medicationList = new ArrayList<>();
    private MedicationAdapter medicationAdapter;

    private static final String TAG = "HomeFragment";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        // Bind UI elements
        calendarRecyclerView = view.findViewById(R.id.calendarRecyclerView);
        textViewSelectedDate = view.findViewById(R.id.textViewSelectedDate);
        medicationRecyclerView = view.findViewById(R.id.medicationRecyclerView);
        textViewNoMedications = view.findViewById(R.id.textViewNoMedications);

        // Set up Horizontal RecyclerView
        layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        calendarRecyclerView.setLayoutManager(layoutManager);

        // Get dates and timestamps
        List<Long> timestampList = getSurroundingDaysTimestamps();
        List<String> dateList = formatTimestampsToDates(timestampList);

        // Initialize CalendarAdapter with updated interface
        CalendarAdapter adapter = new CalendarAdapter(getContext(), dateList, timestampList, (formattedDate, timestamp) -> {
            textViewSelectedDate.setText(formattedDate);
            fetchMedicationsForDate(timestamp);
        });
        calendarRecyclerView.setAdapter(adapter);

        // Scroll to today's position in the middle (position 5)
        calendarRecyclerView.post(() -> {
            int todayPosition = 5; // Today is at index 5
            layoutManager.scrollToPositionWithOffset(todayPosition, calendarRecyclerView.getWidth() / 2);
        });

        // Set initial selected date as today
        String todayFormattedDate = dateList.get(5);
        textViewSelectedDate.setText(todayFormattedDate);
        fetchMedicationsForDate(timestampList.get(5));

        // Set up Medication RecyclerView
        medicationRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        medicationAdapter = new MedicationAdapter(getContext(), medicationList);
        medicationRecyclerView.setAdapter(medicationAdapter);

        // Find the FAB and set its click listener
        FloatingActionButton fab = view.findViewById(R.id.fabAddMedication);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle FAB click
              //  Toast.makeText(getActivity(), "FAB Clicked!", Toast.LENGTH_SHORT).show();

                // Create an Intent to start the target activity
                Intent intent = new Intent(getActivity(), AddMedActivity.class);
                startActivity(intent);
            }
        });

        return view;
    }

    // Generate timestamps for 5 days before and 5 days after today
    private List<Long> getSurroundingDaysTimestamps() {
        List<Long> timestampList = new ArrayList<>();
        Calendar calendar = Calendar.getInstance();

        // Start 5 days before today
        calendar.add(Calendar.DAY_OF_YEAR, -5);

        // Collect timestamps for 11 days
        for (int i = 0; i < 11; i++) {
            timestampList.add(calendar.getTimeInMillis());
            calendar.add(Calendar.DAY_OF_YEAR, 1);
        }

        return timestampList;
    }

    // Format timestamps into "EEE, d MMM" for display
    private List<String> formatTimestampsToDates(List<Long> timestamps) {
        List<String> formattedDates = new ArrayList<>();
        SimpleDateFormat dateFormat = new SimpleDateFormat("EEE, d MMM", Locale.getDefault());

        for (Long timestamp : timestamps) {
            formattedDates.add(dateFormat.format(new Date(timestamp)));
        }

        return formattedDates;
    }

    // Fetch medications for the selected date (using timestamp)
    private void fetchMedicationsForDate(Long selectedTimestamp) {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {
            Toast.makeText(getActivity(), "User not logged in", Toast.LENGTH_SHORT).show();
            return;
        }

        String userId = currentUser.getUid();
        DatabaseReference medsRef = FirebaseDatabase.getInstance()
                .getReference("medications")
                .child(userId);

        medsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                medicationList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Medication medication = snapshot.getValue(Medication.class);

                    if (medication != null) {
                        Log.d(TAG, "Medication Retrieved: " + medication.getName());
                        Log.d(TAG, "Frequency: " + medication.getFrequency());

                        if ("Interval".equalsIgnoreCase(medication.getFrequency())) {
                            processIntervalMedication(medication, selectedTimestamp);
                        } else if ("Twice daily".equalsIgnoreCase(medication.getFrequency())) {
                            addTwiceDailyMedications(medication, selectedTimestamp);
                        } else if (shouldDisplayForDate(medication, selectedTimestamp)) {
                            medicationList.add(medication);
                        }
                    }
                }


                // Update UI
                if (medicationList.isEmpty()) {
                    textViewNoMedications.setVisibility(View.VISIBLE);
                    medicationRecyclerView.setVisibility(View.GONE);
                } else {
                    textViewNoMedications.setVisibility(View.GONE);
                    medicationRecyclerView.setVisibility(View.VISIBLE);
                }

                medicationAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, "Firebase error: " + error.getMessage());
                Toast.makeText(getActivity(), "Error fetching medications: " + error.getMessage(), Toast.LENGTH_LONG).show();
            }

        });
    }

    // Check if medication should display for the selected timestamp
    private boolean shouldDisplayForDate(Medication medication, Long selectedTimestamp) {
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            Date startDate = dateFormat.parse(medication.getStartDate());
            Date endDate = dateFormat.parse(medication.getEndDate());

            if (startDate == null || endDate == null) return false;

            long startTime = startDate.getTime();
            long endTime = endDate.getTime() + (24 * 60 * 60 * 1000L) - 1; // Include the full end day

            // If selected date is outside the range, return false
            if (selectedTimestamp < startTime || selectedTimestamp > endTime) {
                return false;
            }

            if ("Specific days".equalsIgnoreCase(medication.getFrequency())) {
                List<String> selectedDays = getSelectedDaysFromMedication(medication);
                String selectedDay = new SimpleDateFormat("EEE", Locale.getDefault()).format(new Date(selectedTimestamp));
                return selectedDays.contains(selectedDay); // Check if the day matches
            }

            return true; // Allow other types to pass through
        } catch (Exception e) {
            Log.e(TAG, "Error parsing medication dates: " + e.getMessage());
            return false;
        }
    }


    private void processIntervalMedication(Medication medication, Long selectedTimestamp) {
        if (!shouldDisplayForDate(medication, selectedTimestamp)) return;

        try {
            String reminderTime = medication.getReminderTime();
            String[] parts = reminderTime.split(", ");
            int intervalHours = Integer.parseInt(parts[0].split(": ")[1].replace("hours", "").trim());
            String startTimeStr = parts[1].split(": ")[1].trim();
            String endTimeStr = parts[2].split(": ")[1].trim();
            String doseDetails = parts[3].split(": ")[1].trim();

            // Add interval-specific details to the list
            medicationList.add(new Medication(
                    medication.getName(),
                    "Interval Details",
                    "Start: " +startTimeStr+ " End: "+endTimeStr+" Interval: " + intervalHours + " hours, " + "Dosage: "+doseDetails,
                    true,
                    medication.getFrequency(),
                    medication.getRefillAmount(),
                    medication.getRefillThreshold(),
                    medication.getStartDate(),
                    medication.getEndDate()
            ));
        } catch (Exception e) {
            Log.e(TAG, "Error processing interval medication: " + e.getMessage());
        }
    }


    private void addTwiceDailyMedications(Medication medication, Long selectedTimestamp) {
        if (!shouldDisplayForDate(medication, selectedTimestamp)) return;

        if (medication.getFirstIntakeDetails() != null) {
            medicationList.add(new Medication(
                    medication.getName(),
                    "First Intake",
                    medication.getFirstIntakeDetails(),
                    true,
                    medication.getFrequency(),
                    medication.getRefillAmount(),
                    medication.getRefillThreshold(),
                    medication.getStartDate(),
                    medication.getEndDate()
            ));
        }

        if (medication.getSecondIntakeDetails() != null) {
            medicationList.add(new Medication(
                    medication.getName(),
                    "Second Intake",
                    medication.getSecondIntakeDetails(),
                    true,
                    medication.getFrequency(),
                    medication.getRefillAmount(),
                    medication.getRefillThreshold(),
                    medication.getStartDate(),
                    medication.getEndDate()
            ));
        }
    }
    private List<String> getSelectedDaysFromMedication(Medication medication) {
        List<String> days = new ArrayList<>();
        if (medication.getSelectedDays() != null && !medication.getSelectedDays().isEmpty()) {
            String[] daysArray = medication.getSelectedDays().split(", ");
            for (String day : daysArray) {
                days.add(day.trim()); // Clean up extra spaces
            }
        }
        return days;
    }

    @Override
    public void onStart() {
        super.onStart();
        attachChildEventListener();
    }

    @Override
    public void onStop() {
        super.onStop();
        detachChildEventListener();
    }

    private DatabaseReference medsRef;
    private ChildEventListener childEventListener;
    private Set<String> notifiedMedications = new HashSet<>(); // Track notifications

    private void attachChildEventListener() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {
            Toast.makeText(getActivity(), "User not logged in", Toast.LENGTH_SHORT).show();
            return;
        }

        String userId = currentUser.getUid();
        medsRef = FirebaseDatabase.getInstance()
                .getReference("medications")
                .child(userId);

        // Initialize the listener
        childEventListener = new ChildEventListener() {
            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                Medication medication = snapshot.getValue(Medication.class);
                if (medication != null && medication.getRefillAmount() <= medication.getRefillThreshold()) {
                    if (!notifiedMedications.contains(snapshot.getKey())) {
                        sendLocalNotification("Low Refill Alert", "Refill your medication: " + medication.getName());
                        notifiedMedications.add(snapshot.getKey()); // Mark as notified
                    }
                }
            }

            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {}
            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                notifiedMedications.remove(snapshot.getKey()); // Clear notifications if medication is removed
            }
            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {}
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, "Database error: " + error.getMessage());
            }
        };

        medsRef.addChildEventListener(childEventListener);
    }

    private void detachChildEventListener() {
        if (medsRef != null && childEventListener != null) {
            medsRef.removeEventListener(childEventListener);
        }
    }

    @SuppressLint("MissingPermission")
    private void sendLocalNotification(String title, String message) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(getContext(), "med_track_channel")
                .setSmallIcon(R.drawable.logo_launcher)
                .setContentTitle(title)
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_HIGH);

        NotificationManagerCompat manager = NotificationManagerCompat.from(getContext());
        manager.notify(new Random().nextInt(), builder.build());
    }
}
