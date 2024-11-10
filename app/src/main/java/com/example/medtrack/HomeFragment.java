package com.example.medtrack;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class HomeFragment extends Fragment {

    private RecyclerView calendarRecyclerView;
    private TextView textViewSelectedDate;
    private LinearLayoutManager layoutManager;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        calendarRecyclerView = view.findViewById(R.id.calendarRecyclerView);
        textViewSelectedDate = view.findViewById(R.id.textViewSelectedDate);

        // Setup Horizontal RecyclerView
        layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        calendarRecyclerView.setLayoutManager(layoutManager);

        // Get the calendar dates (5 days before to 5 days after today)
        List<String> dateList = getSurroundingDays();

        // Set the adapter with a callback listener
        CalendarAdapter adapter = new CalendarAdapter(getContext(), dateList, new CalendarAdapter.OnDateSelectedListener() {
            @Override
            public void onDateSelected(String selectedDate) {
                // Update the selected date TextView when a date is selected
                textViewSelectedDate.setText(selectedDate);
            }
        });
        calendarRecyclerView.setAdapter(adapter);

        // Scroll to today's position in the middle (position 5)
        calendarRecyclerView.post(() -> {
            int todayPosition = 5; // Since we want to start at today, which is the 6th element (index 5)
            layoutManager.scrollToPositionWithOffset(todayPosition, calendarRecyclerView.getWidth() / 2);
        });

        // Set initial selected date as today
        textViewSelectedDate.setText(dateList.get(5));

        return view;
    }

    // Function to get the surrounding days (5 days before and 5 days after today)
    private List<String> getSurroundingDays() {
        List<String> dateList = new ArrayList<>();
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("EEE, d MMM", Locale.getDefault());

        // Move to 5 days before today
        calendar.add(Calendar.DAY_OF_YEAR, -5);

        // Collect dates from 5 days before to 5 days after today
        for (int i = 0; i < 11; i++) {
            dateList.add(dateFormat.format(calendar.getTime()));
            calendar.add(Calendar.DAY_OF_YEAR, 1);
        }

        return dateList;
    }
}
