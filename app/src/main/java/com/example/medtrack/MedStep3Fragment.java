package com.example.medtrack;

import android.app.TimePickerDialog;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.TimePicker;

import java.util.Calendar;

public class MedStep3Fragment extends Fragment {

    private TextView reminderTimeTextView;
    private Button pickTimeButton;
    private Button nextButton;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_med_step3, container, false);

        reminderTimeTextView = view.findViewById(R.id.textViewReminderTime);
        pickTimeButton = view.findViewById(R.id.buttonPickTime);
        nextButton = view.findViewById(R.id.nextButton);

        pickTimeButton.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            int hour = calendar.get(Calendar.HOUR_OF_DAY);
            int minute = calendar.get(Calendar.MINUTE);

            TimePickerDialog timePickerDialog = new TimePickerDialog(getContext(), (TimePicker view1, int hourOfDay, int minute1) -> {
                String time = String.format("%02d:%02d", hourOfDay, minute1);
                reminderTimeTextView.setText(time);
            }, hour, minute, true);
            timePickerDialog.show();
        });

        nextButton.setOnClickListener(v -> {
            String time = reminderTimeTextView.getText().toString();
            ((AddMedActivity) getActivity()).setReminderTime(time);
            ((AddMedActivity) getActivity()).goToNextStep();
        });

        return view;
    }
}
