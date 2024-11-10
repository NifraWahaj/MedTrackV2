package com.example.medtrack;

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

public class MedStep1Fragment extends Fragment {

    private EditText medicationNameEditText;
    private EditText quantityEditText;
    private Spinner unitSpinner;
    private Button nextButton;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_med_step1, container, false);

        // Bind UI elements
        medicationNameEditText = view.findViewById(R.id.editTextMedicationName);
        quantityEditText = view.findViewById(R.id.editTextQuantity);
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

        // Set click listener for the next button
        nextButton.setOnClickListener(v -> {
            String name = medicationNameEditText.getText().toString().trim();
            String quantityStr = quantityEditText.getText().toString().trim();
            String unit = unitSpinner.getSelectedItem().toString();

            // Validate the inputs
            if (name.isEmpty() || quantityStr.isEmpty()) {
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
            ((AddMedActivity) getActivity()).setMedicationName(name);
            ((AddMedActivity) getActivity()).setMedicationUnit(quantity + " " + unit);

            // Move to next step
            ((AddMedActivity) getActivity()).goToNextStep();
        });

        return view;
    }
}
