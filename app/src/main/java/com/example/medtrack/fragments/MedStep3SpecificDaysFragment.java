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
import android.widget.TextView;
import android.widget.Toast;

import com.example.medtrack.R;
import com.example.medtrack.activities.AddMedActivity;

import java.util.Calendar;
import java.util.List;


public class MedStep3SpecificDaysFragment extends Fragment {

    private TextView selectedDaysTextView;
    private Button buttonPickTime, nextButton;
    private EditText editTextQuantity;
    private Spinner doseSpinner;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_med_step3_specific_days, container, false);

        selectedDaysTextView = view.findViewById(R.id.textViewSelectedDays);
        buttonPickTime = view.findViewById(R.id.buttonPickTime);
        editTextQuantity = view.findViewById(R.id.editTextQuantity);
        doseSpinner = view.findViewById(R.id.spinnerDose);
        nextButton = view.findViewById(R.id.nextButton);

        // Retrieve and display selected days from AddMedActivity
        AddMedActivity activity = (AddMedActivity) getActivity();
        if (activity != null) {
            List<String> selectedDays = activity.getSelectedDays();
            selectedDaysTextView.setText("Intake on " + String.join(", ", selectedDays));
        }

        // Set up Spinner with dose units
        String[] doses = {"Tablet(s)", "Capsule(s)", "mL (milliliters)", "Drop(s)", "Puff(s)", "mg (milligrams)", "tsp (teaspoon)", "Application(s)"};
        ArrayAdapter<String> doseAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, doses);
        doseAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        doseSpinner.setAdapter(doseAdapter);

        // Set up Time Picker for the intake time
        buttonPickTime.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            int hour = calendar.get(Calendar.HOUR_OF_DAY);
            int minute = calendar.get(Calendar.MINUTE);

            TimePickerDialog timePickerDialog = new TimePickerDialog(getContext(), (view1, hourOfDay, minute1) -> {
                String time = String.format("%02d:%02d", hourOfDay, minute1);
                buttonPickTime.setText(time);
            }, hour, minute, true);
            timePickerDialog.show();
        });

        nextButton.setOnClickListener(v -> {
            String time = buttonPickTime.getText().toString();
            String quantityStr = editTextQuantity.getText().toString().trim();
            String unit = doseSpinner.getSelectedItem().toString();

            // Validate the inputs
            if (time.equals("Select Time") || quantityStr.isEmpty()) {
                Toast.makeText(getActivity(), "Please fill in all fields.", Toast.LENGTH_SHORT).show();
                return;
            }

            int quantity;
            try {
                quantity = Integer.parseInt(quantityStr);
            } catch (NumberFormatException e) {
                Toast.makeText(getActivity(), "Please enter a valid quantity.", Toast.LENGTH_SHORT).show();
                return;
            }

            // Save data to activity
            String reminderDetails = "Time: " + time + ", Dosage: " + quantity + " " + unit;
            activity.setReminderTime(reminderDetails);
            activity.goToNextStep();
        });

        return view;
    }
}
