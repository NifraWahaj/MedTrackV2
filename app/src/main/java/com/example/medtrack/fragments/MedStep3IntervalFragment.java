package com.example.medtrack.fragments;

import android.app.TimePickerDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.medtrack.R;
import com.example.medtrack.activities.AddMedActivity;

import java.util.Calendar;


public class MedStep3IntervalFragment extends Fragment {

    private EditText intervalEditText;
    private Button buttonPickStartTime, buttonPickEndTime, nextButton;
    private Spinner doseSpinner;
    private EditText doseQuantityEditText;
    private String startTime, endTime;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_med_step3_interval, container, false);

        // Bind UI elements
        intervalEditText = view.findViewById(R.id.editTextInterval);
        buttonPickStartTime = view.findViewById(R.id.buttonPickStartTime);
        buttonPickEndTime = view.findViewById(R.id.buttonPickEndTime);
        doseSpinner = view.findViewById(R.id.spinnerDose);
        doseQuantityEditText = view.findViewById(R.id.editTextDoseQuantity); // Added dose quantity EditText
        nextButton = view.findViewById(R.id.nextButton);

        // Set up Spinner with dose units
        String[] doses = {"Tablet(s)", "Capsule(s)", "mL (milliliters)", "Drop(s)", "Puff(s)", "mg (milligrams)", "tsp (teaspoon)", "Application(s)"};
        ArrayAdapter<String> doseAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, doses);
        doseAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        doseSpinner.setAdapter(doseAdapter);

        // Set up Time Picker for Start Time
        buttonPickStartTime.setOnClickListener(v -> {
            showTimePicker((time, button) -> {
                buttonPickStartTime.setText(time);
                startTime = time;
            });
        });

        // Set up Time Picker for End Time
        buttonPickEndTime.setOnClickListener(v -> {
            showTimePicker((time, button) -> {
                buttonPickEndTime.setText(time);
                endTime = time;
            });
        });

        // Set up Next Button click
        nextButton.setOnClickListener(v -> {
            String intervalStr = intervalEditText.getText().toString().trim();
            String doseUnit = doseSpinner.getSelectedItem().toString();
            String doseQuantityStr = doseQuantityEditText.getText().toString().trim();

            if (startTime == null || endTime == null || intervalStr.isEmpty() || doseQuantityStr.isEmpty()) {
                Toast.makeText(getActivity(), "Please fill in all fields.", Toast.LENGTH_SHORT).show();
                return;
            }

            int interval, doseQuantity;
            try {
                interval = Integer.parseInt(intervalStr);
                doseQuantity = Integer.parseInt(doseQuantityStr);
            } catch (NumberFormatException e) {
                Toast.makeText(getActivity(), "Please enter valid numbers for interval and dose quantity.", Toast.LENGTH_SHORT).show();
                return;
            }

            int remindersCount = calculateRemindersCount(startTime, endTime, interval);

            if (remindersCount == 0) {
                Toast.makeText(getActivity(), "No reminders fit in the specified time frame.", Toast.LENGTH_SHORT).show();
                return;
            }

            String reminderDetails = "Interval: " + interval + " hours, Start Time: " + startTime + ", End Time: " + endTime + ", Dose: " + doseQuantity + " " + doseUnit;
            ((AddMedActivity) getActivity()).setReminderTime(reminderDetails);
            ((AddMedActivity) getActivity()).goToNextStep();
        });

        return view;
    }

    private void showTimePicker(TimePickerCallback callback) {
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(getContext(), (view, hourOfDay, minute1) -> {
            String time = String.format("%02d:%02d", hourOfDay, minute1);
            callback.onTimePicked(time, buttonPickStartTime);
        }, hour, minute, true);
        timePickerDialog.show();
    }

    private int calculateRemindersCount(String start, String end, int interval) {
        // Parse the start and end time strings to hour and minute
        String[] startSplit = start.split(":");
        String[] endSplit = end.split(":");

        int startHour = Integer.parseInt(startSplit[0]);
        int startMinute = Integer.parseInt(startSplit[1]);
        int endHour = Integer.parseInt(endSplit[0]);
        int endMinute = Integer.parseInt(endSplit[1]);

        // Calculate the total minutes for start and end times
        int startTotalMinutes = startHour * 60 + startMinute;
        int endTotalMinutes = endHour * 60 + endMinute;

        // Ensure start time is before end time
        if (endTotalMinutes <= startTotalMinutes) {
            return 0;
        }

        // Calculate the number of reminders that fit into the specified time range
        int intervalMinutes = interval * 60;
        return (endTotalMinutes - startTotalMinutes) / intervalMinutes;
    }

    private interface TimePickerCallback {
        void onTimePicked(String time, Button button);
    }
}
