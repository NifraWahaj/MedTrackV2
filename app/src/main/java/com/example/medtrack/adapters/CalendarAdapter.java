package com.example.medtrack.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.medtrack.R;

import java.util.List;

public class CalendarAdapter extends RecyclerView.Adapter<CalendarAdapter.CalendarViewHolder> {

    private final List<String> dateList;
    private final Context context;
    private int selectedPosition = 4; // Start with today as selected (position 4)
    private final OnDateSelectedListener dateSelectedListener;

    // Callback interface to communicate selected date
    public interface OnDateSelectedListener {
        void onDateSelected(String selectedDate);
    }

    public CalendarAdapter(Context context, List<String> dateList, OnDateSelectedListener listener) {
        this.context = context;
        this.dateList = dateList;
        this.dateSelectedListener = listener;
    }

    @NonNull
    @Override
    public CalendarViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_calendar_date, parent, false);
        return new CalendarViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CalendarViewHolder holder, int position) {
        String dateInfo = dateList.get(position);
        String[] splitInfo = dateInfo.split(", ");
        String dayName = splitInfo[0];  // e.g., Mon, Tue
        String date = splitInfo[1];      // e.g., 12 Sept

        holder.textViewDayName.setText(dayName);
        holder.textViewDateNumber.setText(date.split(" ")[0]); // Extracting just the number

        // Highlight selected date
        if (selectedPosition == position) {
            holder.textViewDateNumber.setBackgroundResource(R.drawable.bg_circle_date);
            holder.textViewDateNumber.setTextColor(context.getResources().getColor(android.R.color.white));
        } else {
            holder.textViewDateNumber.setBackgroundResource(android.R.color.transparent);
            holder.textViewDateNumber.setTextColor(context.getResources().getColor(android.R.color.black));
        }

        holder.itemView.setOnClickListener(v -> {
            int oldPosition = selectedPosition;
            selectedPosition = position;

            // Notify HomeFragment of the newly selected date
            dateSelectedListener.onDateSelected(dateInfo);

            notifyItemChanged(oldPosition);
            notifyItemChanged(selectedPosition);
        });
    }

    @Override
    public int getItemCount() {
        return dateList.size();
    }

    public static class CalendarViewHolder extends RecyclerView.ViewHolder {
        TextView textViewDayName, textViewDateNumber;

        public CalendarViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewDayName = itemView.findViewById(R.id.textViewDayName);
            textViewDateNumber = itemView.findViewById(R.id.textViewDateNumber);
        }
    }
}
