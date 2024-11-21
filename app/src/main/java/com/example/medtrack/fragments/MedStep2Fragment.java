package com.example.medtrack.fragments;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.medtrack.R;
import com.example.medtrack.activities.AddMedActivity;

import java.util.ArrayList;
import java.util.List;

public class MedStep2Fragment extends Fragment {

    private RadioGroup frequencyRadioGroup;
    private LinearLayout daysSelectionLayout;
    private Button nextButton;
    private List<String> selectedDays = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_med_step2, container, false);

        frequencyRadioGroup = view.findViewById(R.id.radioGroupFrequency);
        daysSelectionLayout = view.findViewById(R.id.daysSelectionLayout);
        nextButton = view.findViewById(R.id.nextButton);

        frequencyRadioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            // Show or hide the days selection layout
            if (checkedId == R.id.radioSpecificDays) {
                daysSelectionLayout.setVisibility(View.VISIBLE);
            } else {
                daysSelectionLayout.setVisibility(View.GONE);
            }
        });

        // Handle day selection
        for (int i = 0; i < daysSelectionLayout.getChildCount(); i++) {
            TextView dayView = (TextView) daysSelectionLayout.getChildAt(i);
            dayView.setOnClickListener(v -> {
                String day = ((TextView) v).getText().toString();
                if (selectedDays.contains(day)) {
                    selectedDays.remove(day);
                    dayView.setBackgroundResource(R.drawable.day_unselected);  // Deselect color
                } else {
                    selectedDays.add(day);
                    dayView.setBackgroundResource(R.drawable.day_selected);  // Select color
                }
            });
        }


        nextButton.setOnClickListener(v -> {
            int selectedId = frequencyRadioGroup.getCheckedRadioButtonId();
            if (selectedId != -1) {
                RadioButton selectedRadioButton = view.findViewById(selectedId);
                String frequency = selectedRadioButton.getText().toString();

                AddMedActivity activity = (AddMedActivity) getActivity();
                if (activity != null) {
                    activity.setMedicationFrequency(frequency);

                    if (frequency.equals("Specific days (e.g., Mon, Wed, Fri)")) {
                        if (selectedDays.isEmpty()) {
                            Toast.makeText(getActivity(), "Please select at least one day.", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        activity.setSelectedDays(new ArrayList<>(selectedDays)); // Ensure selected days are assigned to the activity
                    }

                    activity.updateFragmentsBasedOnFrequency();
                    activity.goToNextStep();
                }
            }
        });


        return view;
    }
}

