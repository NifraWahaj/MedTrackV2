package com.example.medtrack.fragments;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.medtrack.R;
import com.example.medtrack.activities.AddMedActivity;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class MedStep1Fragment extends Fragment {

    private EditText medicationNameEditText;
    private Button nextButton, buttonPickStartDate, buttonPickEndDate;
    private String startDate, endDate;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_med_step1, container, false);

        // Bind UI elements
        medicationNameEditText = view.findViewById(R.id.editTextMedicationName);
        nextButton = view.findViewById(R.id.nextButton);
        buttonPickStartDate = view.findViewById(R.id.buttonPickStartDate);
        buttonPickEndDate = view.findViewById(R.id.buttonPickEndDate);

        // Set up date pickers for start and end dates
        buttonPickStartDate.setOnClickListener(v -> showDatePickerDialog((date) -> {
            startDate = date;
            buttonPickStartDate.setText(date);
        }));

        buttonPickEndDate.setOnClickListener(v -> showDatePickerDialog((date) -> {
            endDate = date;
            buttonPickEndDate.setText(date);
        }));

        // Set click listener for the next button
        nextButton.setOnClickListener(v -> {
            String name = medicationNameEditText.getText().toString().trim();

            // Validate the inputs
            if (name.isEmpty() || startDate == null || endDate == null) {
                Toast.makeText(getActivity(), "Please fill in all fields.", Toast.LENGTH_SHORT).show();
                return;
            }

            // Save data to activity
            AddMedActivity activity = (AddMedActivity) getActivity();
            if (activity != null) {
                activity.setMedicationName(name);
                activity.setStartDate(startDate);
                activity.setEndDate(endDate);

                // Move to next step
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
}
