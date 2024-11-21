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
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.medtrack.R;
import com.example.medtrack.activities.AddMedActivity;

import java.util.Calendar;


public class MedStep3Fragment extends Fragment {

    private TextView reminderTimeTextView;
    private EditText editTextQuantity;
    private Spinner unitSpinner;
    private Button pickTimeButton;
    private Button nextButton;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_med_step3, container, false);

        reminderTimeTextView = view.findViewById(R.id.textViewReminderTime);
        pickTimeButton = view.findViewById(R.id.buttonPickTime);
        editTextQuantity = view.findViewById(R.id.editTextQuantity);
        unitSpinner = view.findViewById(R.id.spinnerUnit);
        nextButton = view.findViewById(R.id.nextButton);

        // Populate the Spinner with the list of units
        String[] units = {
                "Tablet(s)", "Capsule(s)", "mL (milliliters)", "tsp (teaspoon)",
                "Drop(s)", "Puff(s)", "mg (milligrams)", "Application(s)"
        };

        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, units);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        unitSpinner.setAdapter(adapter);

        pickTimeButton.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            int hour = calendar.get(Calendar.HOUR_OF_DAY);
            int minute = calendar.get(Calendar.MINUTE);

            TimePickerDialog timePickerDialog = new TimePickerDialog(getContext(), (TimePicker view1, int hourOfDay, int minute1) -> {
                String time = String.format("%02d:%02d", hourOfDay, minute1);
                reminderTimeTextView.setText(time);
            }, hour, minute, true);
            timePickerDialog.show();
        });

        nextButton.setOnClickListener(v -> {
            String time = reminderTimeTextView.getText().toString();
            String quantityStr = editTextQuantity.getText().toString().trim();
            String unit = unitSpinner.getSelectedItem().toString();

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
            ((AddMedActivity) getActivity()).setReminderTime(time + ", Dosage: " + quantity + " " + unit);
            ((AddMedActivity) getActivity()).goToNextStep();
        });

        return view;
    }
}
