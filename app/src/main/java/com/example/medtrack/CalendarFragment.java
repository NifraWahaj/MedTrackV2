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

        // Get the calendar dates
        List<String> dateList = getNextSevenDays();

        // Set the adapter
        CalendarAdapter adapter = new CalendarAdapter(getContext(), dateList);
        calendarRecyclerView.setAdapter(adapter);

        return view;
    }

    // Function to get the next seven days
    private List<String> getNextSevenDays() {
        List<String> dateList = new ArrayList<>();
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("EEE, d MMM", Locale.getDefault());

        for (int i = 0; i < 7; i++) {
            dateList.add(dateFormat.format(calendar.getTime()));
            calendar.add(Calendar.DAY_OF_YEAR, 1);
        }

        return dateList;
    }
}
