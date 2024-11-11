package com.example.medtrack;

import android.app.TimePickerDialog;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.Calendar;

public class MedStepIntervalFragment extends Fragment {

    private Button buttonPickStartTime, buttonPickEndTime, nextButton;
    private EditText editTextIntervalHours;

    private String startTime, endTime;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_med_step_interval, container, false);

        buttonPickStartTime = view.findViewById(R.id.buttonPickStartTime);
        buttonPickEndTime = view.findViewById(R.id.buttonPickEndTime);
        editTextIntervalHours = view.findViewById(R.id.editTextIntervalHours);
        nextButton = view.findViewById(R.id.nextButton);

        // Set up start time picker
        buttonPickStartTime.setOnClickListener(v -> showTimePicker((view1, hourOfDay, minute) -> {
            startTime = String.format("%02d:%02d %s",
                    (hourOfDay % 12 == 0) ? 12 : hourOfDay % 12,
                    minute,
                    hourOfDay < 12 ? "am" : "pm");
            buttonPickStartTime.setText(startTime);
        }));

        // Set up end time picker
        buttonPickEndTime.setOnClickListener(v -> showTimePicker((view1, hourOfDay, minute) -> {
            endTime = String.format("%02d:%02d %s",
                    (hourOfDay % 12 == 0) ? 12 : hourOfDay % 12,
                    minute,
                    hourOfDay < 12 ? "am" : "pm");
            buttonPickEndTime.setText(endTime);
        }));

        // Handle Next button click
        nextButton.setOnClickListener(v -> {
            String intervalStr = editTextIntervalHours.getText().toString();

            if (startTime == null || endTime == null || intervalStr.isEmpty()) {
                Toast.makeText(getActivity(), "Please provide start time, end time, and interval", Toast.LENGTH_SHORT).show();
                return;
            }

            int intervalHours;
            try {
                intervalHours = Integer.parseInt(intervalStr);
            } catch (NumberFormatException e) {
                Toast.makeText(getActivity(), "Please enter a valid number for interval hours", Toast.LENGTH_SHORT).show();
                return;
            }

            // Save data to activity
            AddMedActivity activity = (AddMedActivity) getActivity();
            if (activity != null) {
                activity.setIntervalDetails(startTime, endTime, intervalHours);
                activity.goToNextStep();
            }
        });

        return view;
    }

    private void showTimePicker(TimePickerDialog.OnTimeSetListener timeSetListener) {
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        new TimePickerDialog(getContext(), timeSetListener, hour, minute, false).show();
    }
}
