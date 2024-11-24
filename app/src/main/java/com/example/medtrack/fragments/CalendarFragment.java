package com.example.medtrack.fragments;

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

import com.example.medtrack.R;
import com.example.medtrack.adapters.CalendarAdapter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class CalendarFragment extends Fragment {

    private RecyclerView calendarRecyclerView;
    private TextView textViewSelectedDate;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_calendar, container, false);

        calendarRecyclerView = view.findViewById(R.id.calendarRecyclerView);
        textViewSelectedDate = view.findViewById(R.id.textViewSelectedDate);

        // Setup Horizontal RecyclerView
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        calendarRecyclerView.setLayoutManager(layoutManager);

        // Get dates and timestamps
        List<Long> timestampList = getNextSevenDaysTimestamps();
        List<String> dateList = formatTimestampsToDates(timestampList);

        // Set the adapter with a callback listener
        CalendarAdapter adapter = new CalendarAdapter(getContext(), dateList, timestampList, new CalendarAdapter.OnDateSelectedListener() {
            @Override
            public void onDateSelected(String formattedDate, Long timestamp) {
                // Update the selected date TextView when a date is selected
                textViewSelectedDate.setText(formattedDate);
            }
        });
        calendarRecyclerView.setAdapter(adapter);

        return view;
    }

    // Generate timestamps for the next 7 days
    private List<Long> getNextSevenDaysTimestamps() {
        List<Long> timestampList = new ArrayList<>();
        Calendar calendar = Calendar.getInstance();

        for (int i = 0; i < 7; i++) {
            timestampList.add(calendar.getTimeInMillis());
            calendar.add(Calendar.DAY_OF_YEAR, 1);
        }

        return timestampList;
    }

    // Format timestamps into "EEE, d MMM" for display
    private List<String> formatTimestampsToDates(List<Long> timestamps) {
        List<String> formattedDates = new ArrayList<>();
        SimpleDateFormat dateFormat = new SimpleDateFormat("EEE, d MMM", Locale.getDefault());

        for (Long timestamp : timestamps) {
            formattedDates.add(dateFormat.format(new Date(timestamp)));
        }

        return formattedDates;
    }
}
