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
import android.widget.ProgressBar;

import com.example.medtrack.R;
import com.example.medtrack.activities.AddMedActivity;
import android.widget.ProgressBar;
public class MedStep4Fragment extends Fragment {

    private EditText currentInventoryEditText;
    private EditText thresholdEditText;
    private Button nextButton;
    private ProgressBar progressBar;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_med_step4, container, false);

        currentInventoryEditText = view.findViewById(R.id.editTextCurrentInventory);
        thresholdEditText = view.findViewById(R.id.editTextThreshold);
        nextButton = view.findViewById(R.id.nextButton);
        progressBar = view.findViewById(R.id.progress_bar);


        nextButton.setOnClickListener(v -> {
            progressBar.setVisibility(View.VISIBLE); // Show progress bar initially
            boolean isValid = true;

            try {
                String currentInventoryStr = currentInventoryEditText.getText().toString().trim();
                String thresholdStr = thresholdEditText.getText().toString().trim();

                // Validate Current Inventory
                if (currentInventoryStr.isEmpty()) {
                    shakeView(currentInventoryEditText); // Shake the current inventory field
                    currentInventoryEditText.setError("Current inventory is required");
                    isValid = false;
                }

                // Validate Threshold
                if (thresholdStr.isEmpty()) {
                    shakeView(thresholdEditText); // Shake the threshold field
                    thresholdEditText.setError("Threshold value is required");
                    isValid = false;
                }

                // If fields are empty or invalid, stop further processing
                if (!isValid) {
                    progressBar.setVisibility(View.GONE); // Hide progress bar on validation failure
                    return;
                }

                int currentInventory = Integer.parseInt(currentInventoryStr);
                int threshold = Integer.parseInt(thresholdStr);

                AddMedActivity activity = (AddMedActivity) getActivity();
                if (activity != null) {
                    activity.setRefillAmount(currentInventory);
                    activity.setRefillThreshold(threshold);

                    // Save medication data to Firebase after final step
                    activity.saveMedicationToFirebase();
                }

            } catch (NumberFormatException e) {
                // Handle incorrect input format
                thresholdEditText.setError("Please enter a valid number");
                currentInventoryEditText.setError("Please enter a valid number");
                progressBar.setVisibility(View.GONE); // Hide progress bar in case of exception
            }

            progressBar.setVisibility(View.GONE); // Hide progress bar after successful processing
        });



        return view;
    }
    private void shakeView(View view) {
        android.view.animation.Animation shake = android.view.animation.AnimationUtils.loadAnimation(getContext(), R.anim.shake);
        view.startAnimation(shake);
    }

}