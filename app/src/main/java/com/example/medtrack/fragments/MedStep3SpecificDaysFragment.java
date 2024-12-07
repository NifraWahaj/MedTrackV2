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
        String[] doses = {
                "Tablet(s)", "Capsule(s)", "mL", "tsp",
                "Drop(s)", "Puff(s)", "mg", "Application(s)"
        };
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
            boolean isValid = true;

            // Validate Time Selection
            String time = buttonPickTime.getText().toString();
            if (time.equals("select")) { //TODO: NOT WORKING
                shakeView(buttonPickTime); // Shake the "Select Time" button
                Toast.makeText(getActivity(), "Please select a time.", Toast.LENGTH_SHORT).show();
                isValid = false;
            }

            // Validate Dose Quantity
            String quantityStr = editTextQuantity.getText().toString().trim();
            if (quantityStr.isEmpty()) {
                //    shakeView(editTextQuantity); // Shake the dose quantity field
                editTextQuantity.setError("Dose quantity is required");
                isValid = false;
            }

            // Validate Spinner Selection
            String unit = doseSpinner.getSelectedItem().toString();
            if (unit.equals("Select Unit")) { // Assuming "Select Unit" is the default option
                shakeView(doseSpinner); // Shake the dose spinner
                Toast.makeText(getActivity(), "Please select a dose unit.", Toast.LENGTH_SHORT).show();
                isValid = false;
            }

            if (!isValid) {
                return; // Prevent moving to the next step if there are errors
            }

            // Parse and validate dose quantity
            int quantity;
            try {
                quantity = Integer.parseInt(quantityStr);
            } catch (NumberFormatException e) {
                shakeView(editTextQuantity); // Shake the quantity field for invalid input
                Toast.makeText(getActivity(), "Please enter a valid quantity.", Toast.LENGTH_SHORT).show();
                return;
            }


            // Save data to activity
            String reminderDetails = time + ", Dosage: " + quantity + " " + unit;
            activity.setReminderTime(reminderDetails);
            activity.goToNextStep();

        });


        return view;
    }
    private void shakeView(View view) {
        android.view.animation.Animation shake = android.view.animation.AnimationUtils.loadAnimation(getContext(), R.anim.shake);
        view.startAnimation(shake);
    }

}