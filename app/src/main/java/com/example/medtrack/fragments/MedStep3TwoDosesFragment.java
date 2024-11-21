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

public class MedStep3TwoDosesFragment extends Fragment {

    private Button buttonPickTimeFirstIntake, buttonPickTimeSecondIntake, nextButton;
    private EditText editTextQuantityFirstIntake, editTextQuantitySecondIntake;
    private Spinner spinnerUnitFirstIntake, spinnerUnitSecondIntake;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_med_step3_two_doses, container, false);

        // Bind UI elements
        buttonPickTimeFirstIntake = view.findViewById(R.id.buttonPickTimeFirstIntake);
        buttonPickTimeSecondIntake = view.findViewById(R.id.buttonPickTimeSecondIntake);
        editTextQuantityFirstIntake = view.findViewById(R.id.editTextQuantityFirstIntake);
        editTextQuantitySecondIntake = view.findViewById(R.id.editTextQuantitySecondIntake);
        spinnerUnitFirstIntake = view.findViewById(R.id.spinnerUnitFirstIntake);
        spinnerUnitSecondIntake = view.findViewById(R.id.spinnerUnitSecondIntake);
        nextButton = view.findViewById(R.id.nextButton);

        // Populate the Spinners with the list of units
        String[] units = {
                "Tablet(s)", "Capsule(s)", "mL (milliliters)", "tsp (teaspoon)",
                "Drop(s)", "Puff(s)", "mg (milligrams)", "Application(s)"
        };

        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, units);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerUnitFirstIntake.setAdapter(adapter);
        spinnerUnitSecondIntake.setAdapter(adapter);

        // Set up time picker for First Intake
        buttonPickTimeFirstIntake.setOnClickListener(v -> {
            showTimePicker(buttonPickTimeFirstIntake);
        });

        // Set up time picker for Second Intake
        buttonPickTimeSecondIntake.setOnClickListener(v -> {
            showTimePicker(buttonPickTimeSecondIntake);
        });

        // Handle Next button click
        nextButton.setOnClickListener(v -> {
            String firstTime = buttonPickTimeFirstIntake.getText().toString();
            String firstQuantityStr = editTextQuantityFirstIntake.getText().toString();
            String firstUnit = spinnerUnitFirstIntake.getSelectedItem().toString();

            String secondTime = buttonPickTimeSecondIntake.getText().toString();
            String secondQuantityStr = editTextQuantitySecondIntake.getText().toString();
            String secondUnit = spinnerUnitSecondIntake.getSelectedItem().toString();

            if (firstTime.equals("Select Time") || firstQuantityStr.isEmpty() || secondTime.equals("Select Time") || secondQuantityStr.isEmpty()) {
                Toast.makeText(getActivity(), "Please fill in all fields for both intakes.", Toast.LENGTH_SHORT).show();
                return;
            }

            int firstQuantity, secondQuantity;
            try {
                firstQuantity = Integer.parseInt(firstQuantityStr);
                secondQuantity = Integer.parseInt(secondQuantityStr);
            } catch (NumberFormatException e) {
                Toast.makeText(getActivity(), "Please enter valid quantities.", Toast.LENGTH_SHORT).show();
                return;
            }

            // Save data to activity
            ((AddMedActivity) getActivity()).setFirstIntakeDetails(firstTime, firstQuantity + " " + firstUnit);
            ((AddMedActivity) getActivity()).setSecondIntakeDetails(secondTime, secondQuantity + " " + secondUnit);

            // Move to next step
            ((AddMedActivity) getActivity()).goToNextStep();
        });

        return view;
    }

    private void showTimePicker(Button button) {
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(getContext(), (view, hourOfDay, minute1) -> {
            String time = String.format("%02d:%02d", hourOfDay, minute1);
            button.setText(time);
        }, hour, minute, true);
        timePickerDialog.show();
    }
}