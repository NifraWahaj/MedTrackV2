package com.example.medtrack.fragments;

import android.app.DatePickerDialog;
import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.medtrack.R;
import com.example.medtrack.activities.AddMedActivity;
import com.example.medtrack.utils.ValidationUtils;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class MedStep1Fragment extends Fragment {

    private AutoCompleteTextView medicationNameAutoComplete;
    private Button nextButton, buttonPickStartDate, buttonPickEndDate, buttonEnter;
    private String startDate, endDate;
    private List<String> medicationNames = new ArrayList<>();
    private DatabaseReference allMedsRef;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_med_step1, container, false);

        // Bind UI elements
        medicationNameAutoComplete = view.findViewById(R.id.autoCompleteMedicationName);
        nextButton = view.findViewById(R.id.nextButton);
        buttonPickStartDate = view.findViewById(R.id.buttonPickStartDate);
        buttonPickEndDate = view.findViewById(R.id.buttonPickEndDate);
        buttonEnter = view.findViewById(R.id.buttonEnter);

        // Set up date pickers for start and end dates
        buttonPickStartDate.setOnClickListener(v -> showDatePickerDialog(date -> {
            startDate = date;
            buttonPickStartDate.setText(date);
        }));

        buttonPickEndDate.setOnClickListener(v -> showDatePickerDialog(date -> {
            endDate = date;
            buttonPickEndDate.setText(date);
        }));

        // Fetch medications from Firebase
        allMedsRef = FirebaseDatabase.getInstance().getReference("allMedications");
        allMedsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot medSnapshot : snapshot.getChildren()) {
                    medicationNames.add(medSnapshot.getKey());
                }

                // Set up AutoCompleteTextView adapter
                ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(),
                        android.R.layout.simple_dropdown_item_1line, medicationNames);
                medicationNameAutoComplete.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Failed to load medications", Toast.LENGTH_SHORT).show();
            }
        });

        // Handle "Enter" button click
        buttonEnter.setOnClickListener(v -> {
            String inputName = medicationNameAutoComplete.getText().toString().trim();

            if (inputName.isEmpty()) {
                Toast.makeText(getContext(), "Please enter a medication name", Toast.LENGTH_SHORT).show();
                return;
            }

            if (medicationNames.contains(inputName)) {
                // Save data and proceed
                AddMedActivity activity = (AddMedActivity) getActivity();
                if (activity != null) {
                    activity.setMedicationName(inputName);
                    Toast.makeText(getContext(), "Medication selected: " + inputName, Toast.LENGTH_SHORT).show();
                }
            } else {
                // Show alert to add new medication
                new AlertDialog.Builder(getContext())
                        .setTitle("Medication Not Found")
                        .setMessage("This medication is not in the list. Do you want to add it?")
                        .setPositiveButton("Yes", (dialog, which) -> {
                            // Add new medication to Firebase and update local list
                            allMedsRef.child(inputName).setValue(true).addOnSuccessListener(aVoid -> {
                                medicationNames.add(inputName);
                                medicationNameAutoComplete.setText(inputName);
                                Toast.makeText(getContext(), "Medication added", Toast.LENGTH_SHORT).show();
                            }).addOnFailureListener(e -> {
                                Toast.makeText(getContext(), "Failed to add medication", Toast.LENGTH_SHORT).show();
                            });
                        })
                        .setNegativeButton("No", (dialog, which) -> dialog.dismiss())
                        .show();
            }
        });

        buttonEnter.setOnClickListener(v->{
            // Validate medication name
            String name = medicationNameAutoComplete.getText().toString().trim();
            if (!ValidationUtils.isNonEmpty(name)) {
                medicationNameAutoComplete.setError("Medication name is required");
                medicationNameAutoComplete.requestFocus();
            } else {
                medicationNameAutoComplete.setError(null); // Clear any previous error
            }
        });

        // Set click listener for the next button
        nextButton.setOnClickListener(v -> {
            boolean isValid = true;

            // Validate medication name
            String name = medicationNameAutoComplete.getText().toString().trim();
            if (!ValidationUtils.isNonEmpty(name)) {
                medicationNameAutoComplete.setError("Medication name is required");
                medicationNameAutoComplete.requestFocus();
                isValid = false;
            } else {
                medicationNameAutoComplete.setError(null); // Clear any previous error
            }

            // Validate start date
            if (startDate == null) {
                shakeView(buttonPickStartDate); // Shake the button
                Toast.makeText(getContext(), "Start Date is required", Toast.LENGTH_SHORT).show();
                isValid = false;
            }

            // Validate end date
            if (endDate == null) {
                shakeView(buttonPickEndDate); // Shake the button
                Toast.makeText(getContext(), "End Date is required", Toast.LENGTH_SHORT).show();
                isValid = false;
            }

            // Check if startDate and endDate are valid
            if (startDate != null && endDate != null && !ValidationUtils.isValidDateRange(startDate, endDate)) {
                Toast.makeText(getActivity(), "Invalid date range.", Toast.LENGTH_SHORT).show();
                shakeView(buttonPickStartDate);
                shakeView(buttonPickEndDate);
                isValid = false;
            }

            if (!isValid) {
                return; // Prevent moving to the next step if there are errors
            }

            // Save data to activity
            AddMedActivity activity = (AddMedActivity) getActivity();
            if (activity != null) {
                activity.setMedicationName(name);
                activity.setStartDate(startDate);
                activity.setEndDate(endDate);

                // Move to the next step
                activity.goToNextStep();
            }
        });

        return view;
    }

    private void showDatePickerDialog(OnDateSetListener listener) {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(), (view, year1, month1, dayOfMonth) -> {
            String date = formatDate(year1, month1, dayOfMonth);
            listener.onDateSet(date);
        }, year, month, day);
        datePickerDialog.show();
    }

    private String formatDate(int year, int month, int day) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, day);

        // Format date as "dd/MM/yyyy"
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        return dateFormat.format(calendar.getTime());
    }

    private interface OnDateSetListener {
        void onDateSet(String date);
    }

    private void shakeView(View view) {
        android.view.animation.Animation shake = android.view.animation.AnimationUtils.loadAnimation(getContext(), R.anim.shake);
        view.startAnimation(shake);
    }
}
