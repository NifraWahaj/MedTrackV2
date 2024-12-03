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
            boolean isValid = true;

            // Validate First Intake Time
            String firstTime = buttonPickTimeFirstIntake.getText().toString();
            if (firstTime.equals("Select Time")) { // Assuming "Select Time" is the default text
                shakeView(buttonPickTimeFirstIntake); // Shake the First Intake button
                Toast.makeText(getActivity(), "Please select the time for the first intake.", Toast.LENGTH_SHORT).show();
                isValid = false;
            }

            // Validate Second Intake Time
            String secondTime = buttonPickTimeSecondIntake.getText().toString();
            if (secondTime.equals("Select Time")) {
                shakeView(buttonPickTimeSecondIntake); // Shake the Second Intake button
                Toast.makeText(getActivity(), "Please select the time for the second intake.", Toast.LENGTH_SHORT).show();
                isValid = false;
            }

            // Validate First Intake Quantity
            String firstQuantityStr = editTextQuantityFirstIntake.getText().toString().trim();
            if (firstQuantityStr.isEmpty()) {
                shakeView(editTextQuantityFirstIntake); // Shake the First Intake Quantity field
                editTextQuantityFirstIntake.setError("Quantity is required for the first intake.");
                isValid = false;
            }

            // Validate Second Intake Quantity
            String secondQuantityStr = editTextQuantitySecondIntake.getText().toString().trim();
            if (secondQuantityStr.isEmpty()) {
                shakeView(editTextQuantitySecondIntake); // Shake the Second Intake Quantity field
                editTextQuantitySecondIntake.setError("Quantity is required for the second intake.");
                isValid = false;
            }

            // Validate First Intake Unit Spinner
            String firstUnit = spinnerUnitFirstIntake.getSelectedItem().toString();
            if (firstUnit.equals("Select Unit")) { // Assuming "Select Unit" is the default option
                shakeView(spinnerUnitFirstIntake); // TODO: DOES NOT WORK
                Toast.makeText(getActivity(), "Please select a unit for the first intake.", Toast.LENGTH_SHORT).show();
                isValid = false;
            }

            // Validate Second Intake Unit Spinner
            String secondUnit = spinnerUnitSecondIntake.getSelectedItem().toString();
            if (secondUnit.equals("Select Unit")) {
                shakeView(spinnerUnitSecondIntake); // Shake the Second Unit Spinner
                Toast.makeText(getActivity(), "Please select a unit for the second intake.", Toast.LENGTH_SHORT).show();
                isValid = false;
            }

            if (!isValid) {
                return; // Prevent moving to the next step if there are errors
            }

            // Parse and validate quantities
            int firstQuantity, secondQuantity;
            try {
                firstQuantity = Integer.parseInt(firstQuantityStr);
                secondQuantity = Integer.parseInt(secondQuantityStr);
            } catch (NumberFormatException e) {
                Toast.makeText(getActivity(), "Please enter valid quantities.", Toast.LENGTH_SHORT).show();
                return;
            }

            // Save data to activity
            AddMedActivity activity = (AddMedActivity) getActivity();
            if (activity != null) {
                activity.setFirstIntakeDetails(firstTime, firstQuantity + " " + firstUnit);
                activity.setSecondIntakeDetails(secondTime, secondQuantity + " " + secondUnit);

                // Move to the next step
                activity.goToNextStep();
            }
        });


        return view;
    }
    private void shakeView(View view) {
        android.view.animation.Animation shake = android.view.animation.AnimationUtils.loadAnimation(getContext(), R.anim.shake);
        view.startAnimation(shake);
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