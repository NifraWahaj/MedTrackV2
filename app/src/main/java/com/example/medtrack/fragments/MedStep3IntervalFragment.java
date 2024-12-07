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
import android.widget.Spinner;
import android.widget.EditText;
import android.widget.Toast;

import com.example.medtrack.R;
import com.example.medtrack.activities.AddMedActivity;

import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

public class MedStep3IntervalFragment extends Fragment {

    private Spinner spinnerInterval;
    private Button buttonPickStartTime, buttonPickEndTime, nextButton;
    private Spinner doseSpinner;
    private EditText doseQuantityEditText;
    private String startTime, endTime;
    int startTotalMinutes ;
    int endTotalMinutes ;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_med_step3_interval, container, false);

        // Bind UI elements
        spinnerInterval = view.findViewById(R.id.spinnerInterval);
        buttonPickStartTime = view.findViewById(R.id.buttonPickStartTime);
        buttonPickEndTime = view.findViewById(R.id.buttonPickEndTime);
        doseSpinner = view.findViewById(R.id.spinnerDose);
        doseQuantityEditText = view.findViewById(R.id.editTextDoseQuantity);
        nextButton = view.findViewById(R.id.nextButton);



        // Populate Spinner with interval options
        List<String> intervalOptions = Arrays.asList("1", "2", "3", "4", "5", "6", "7","8","9","10","11");
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_spinner_item,
                intervalOptions
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerInterval.setAdapter(adapter);

        // Set up Spinner with dose units
        String[] doses = {
                "Tablet(s)", "Capsule(s)", "mL", "tsp",
                "Drop(s)", "Puff(s)", "mg", "Application(s)"
        };
        ArrayAdapter<String> doseAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, doses);
        doseAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        doseSpinner.setAdapter(doseAdapter);

        // Set up Time Picker for Start Time
        buttonPickStartTime.setOnClickListener(v -> showTimePicker(time -> {
            buttonPickStartTime.setText(time);
            startTime = time;
        }));

        // Set up Time Picker for End Time
        buttonPickEndTime.setOnClickListener(v -> showTimePicker(time -> {
            buttonPickEndTime.setText(time);
            endTime = time;
        }));

        // Set up Next Button click
        nextButton.setOnClickListener(v -> {
            boolean isValid = true;

            // Validate Start Time
            if (startTime == null) {
                shakeView(buttonPickStartTime); // Shake the Start Time button
                Toast.makeText(getActivity(), "Please select a start time.", Toast.LENGTH_SHORT).show();
                isValid = false;
            }

            // Validate End Time
            if (endTime == null) {
                shakeView(buttonPickEndTime); // Shake the End Time button
                Toast.makeText(getActivity(), "Please select an end time.", Toast.LENGTH_SHORT).show();
                isValid = false;
            }

            // Validate Interval Spinner
            String intervalStr = spinnerInterval.getSelectedItem().toString();
            if (intervalStr.isEmpty()) {
                shakeView(spinnerInterval); // Shake the interval spinner
                Toast.makeText(getActivity(), "Please select an interval.", Toast.LENGTH_SHORT).show();
                isValid = false;
            }

            // Validate Dose Quantity
            String doseQuantityStr = doseQuantityEditText.getText().toString().trim();
            if (doseQuantityStr.isEmpty()) {
                shakeView(doseQuantityEditText); // Shake the dose quantity field
                doseQuantityEditText.setError("Dose quantity is required");
                isValid = false;
            }

            // Validate Dose Spinner
            String doseUnit = doseSpinner.getSelectedItem().toString();
            if (doseUnit.isEmpty() || doseUnit.equals("Select Unit")) { // Assuming "Select Unit" is the default
                shakeView(doseSpinner); // Shake the dose spinner
                Toast.makeText(getActivity(), "Please select a dose unit.", Toast.LENGTH_SHORT).show();
                isValid = false;
            }

            // Return if any validation failed
            if (!isValid) {
                return;
            }

            // Parse interval and dose quantity
            int interval, doseQuantity;
            try {
                interval = Integer.parseInt(intervalStr);
                doseQuantity = Integer.parseInt(doseQuantityStr);
            } catch (NumberFormatException e) {
                Toast.makeText(getActivity(), "Please enter valid numbers for interval and dose quantity.", Toast.LENGTH_SHORT).show();
                return;
            }

            // Validate reminders count
            int remindersCount = calculateRemindersCount(startTime, endTime, interval);
            if (remindersCount == 0) {
                Toast.makeText(getActivity(), "No reminders fit in the specified time frame.", Toast.LENGTH_SHORT).show();
                return;
            } else if (interval > (endTotalMinutes - startTotalMinutes) / 60) {
                Toast.makeText(getActivity(), "Interval too large for the selected time range.", Toast.LENGTH_SHORT).show();
                return;
            }

            // Save data and proceed
            String reminderDetails = "Interval: " + interval + " hours, Start Time: " + startTime + ", End Time: " + endTime + ", Dose: " + doseQuantity + " " + doseUnit;
            AddMedActivity activity = (AddMedActivity) getActivity();
            if (activity != null) {
                activity.setReminderTime(reminderDetails);
                activity.goToNextStep();
            }
        });


        return view;
    }

    private void showTimePicker(TimePickerCallback callback) {
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(getContext(), (view, hourOfDay, minute1) -> {
            String time = String.format("%02d:%02d", hourOfDay, minute1);
            callback.onTimePicked(time);
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
         startTotalMinutes = startHour * 60 + startMinute;
         endTotalMinutes = endHour * 60 + endMinute;

        // Ensure start time is before end time
        if (endTotalMinutes <= startTotalMinutes) {
            return 0;
        }

        // Calculate the number of reminders that fit into the specified time range
        int intervalMinutes = interval * 60;
        return (endTotalMinutes - startTotalMinutes) / intervalMinutes;
    }

    private interface TimePickerCallback {
        void onTimePicked(String time);
    }

    private void shakeView(View view) {
        android.view.animation.Animation shake = android.view.animation.AnimationUtils.loadAnimation(getContext(), R.anim.shake);
        view.startAnimation(shake);
    }


}
