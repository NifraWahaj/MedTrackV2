package com.example.medtrack;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

public class MedStep4Fragment extends Fragment {

    private EditText currentInventoryEditText;
    private EditText thresholdEditText;
    private Button nextButton;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_med_step4, container, false);

        currentInventoryEditText = view.findViewById(R.id.editTextCurrentInventory);
        thresholdEditText = view.findViewById(R.id.editTextThreshold);
        nextButton = view.findViewById(R.id.nextButton);

        nextButton.setOnClickListener(v -> {
            int currentInventory = Integer.parseInt(currentInventoryEditText.getText().toString());
            int threshold = Integer.parseInt(thresholdEditText.getText().toString());

            ((AddMedActivity) getActivity()).setRefillAmount(currentInventory);
            ((AddMedActivity) getActivity()).setRefillThreshold(threshold);

            ((AddMedActivity) getActivity()).goToNextStep();
        });

        return view;
    }
}
