package com.example.medtrack;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;

public class MedStep2Fragment extends Fragment {

    private RadioGroup frequencyRadioGroup;
    private Button nextButton;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_med_step2, container, false);

        frequencyRadioGroup = view.findViewById(R.id.radioGroupFrequency);
        nextButton = view.findViewById(R.id.nextButton);

        nextButton.setOnClickListener(v -> {
            // Get selected frequency
            int selectedId = frequencyRadioGroup.getCheckedRadioButtonId();
            if (selectedId != -1) {
                RadioButton selectedRadioButton = view.findViewById(selectedId);
                String frequency = selectedRadioButton.getText().toString();

                // Save data to activity
                ((AddMedActivity) getActivity()).setFrequency(frequency);

                // Move to next step
                ((AddMedActivity) getActivity()).goToNextStep();
            }
        });

        return view;
    }
}
