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

public class MedStepMoreOptionsFragment extends Fragment {

    private RadioGroup scheduleOptionsRadioGroup;
    private Button nextButton;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_med_step_more_options, container, false);

        scheduleOptionsRadioGroup = view.findViewById(R.id.radioGroupScheduleOptions);
        nextButton = view.findViewById(R.id.nextButton);

        nextButton.setOnClickListener(v -> {
            int selectedId = scheduleOptionsRadioGroup.getCheckedRadioButtonId();
            if (selectedId != -1) {
                RadioButton selectedRadioButton = view.findViewById(selectedId);
                String selectedOption = selectedRadioButton.getText().toString();

                AddMedActivity activity = (AddMedActivity) getActivity();
                if (activity != null) {
                    // Navigate based on selected option
                    switch (selectedOption) {
                        case "Interval (e.g., every X hours, every X days)":
                            activity.goToIntervalFragment();
                            break;
                        case "Multiple times daily (e.g., 3 or more times a day)":
                            activity.goToMultipleTimesFragment();
                            break;
                        case "Specific days of the week (e.g., Mon, Wed, Fri)":
                            activity.goToSpecificDaysFragment();
                            break;
                        case "Cyclic mode (e.g., 21 days intake, 7 days pause)":
                            activity.goToCyclicModeFragment();
                            break;
                    }
                }
            }
        });

        return view;
    }

}
