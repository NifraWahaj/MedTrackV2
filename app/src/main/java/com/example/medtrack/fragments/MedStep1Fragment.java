package com.example.medtrack.fragments;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.medtrack.R;
import com.example.medtrack.activities.AddMedActivity;


public class MedStep1Fragment extends Fragment {

    private EditText medicationNameEditText;
    private Button nextButton;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_med_step1, container, false);

        // Bind UI elements
        medicationNameEditText = view.findViewById(R.id.editTextMedicationName);
        nextButton = view.findViewById(R.id.nextButton);

        // Set click listener for the next button
        nextButton.setOnClickListener(v -> {
            String name = medicationNameEditText.getText().toString().trim();

            // Validate the inputs
            if (name.isEmpty()) {
                Toast.makeText(getActivity(), "Please fill in the medication name.", Toast.LENGTH_SHORT).show();
                return;
            }

            // Save data to activity
            ((AddMedActivity) getActivity()).setMedicationName(name);

            // Move to next step
            ((AddMedActivity) getActivity()).goToNextStep();
        });

        return view;
    }
}
