package com.example.medtrack;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class CalendarAdapter extends RecyclerView.Adapter<CalendarAdapter.CalendarViewHolder> {

    private final List<String> dateList;
    private final Context context;
    private int selectedPosition = -1; // To highlight the selected date

    public CalendarAdapter(Context context, List<String> dateList) {
        this.context = context;
        this.dateList = dateList;
    }

    @NonNull
    @Override
    public CalendarViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_calendar_date, parent, false);
        return new CalendarViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CalendarViewHolder holder, @SuppressLint("RecyclerView") int position) {
        String date = dateList.get(position);
        holder.textViewDate.setText(date);

        // Highlight the selected date
        holder.itemView.setBackgroundResource(
                selectedPosition == position ? R.drawable.selected_date_background : android.R.color.transparent
        );

        holder.itemView.setOnClickListener(v -> {
            selectedPosition = position;
            notifyDataSetChanged(); // Refresh the list to update the background
        });
    }

    @Override
    public int getItemCount() {
        return dateList.size();
    }

    public static class CalendarViewHolder extends RecyclerView.ViewHolder {
        TextView textViewDate;

        public CalendarViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewDate = itemView.findViewById(R.id.textViewDate);
        }
    }
}
