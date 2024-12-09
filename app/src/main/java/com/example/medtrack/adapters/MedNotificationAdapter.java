package com.example.medtrack.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.medtrack.R;
import com.example.medtrack.models.NotificationModel;
import com.example.medtrack.models.NotificationModelMed;

import java.util.List;
public class MedNotificationAdapter extends RecyclerView.Adapter<MedNotificationAdapter.ViewHolder> {

    private final List<NotificationModelMed> notifications;

    public MedNotificationAdapter(List<NotificationModelMed> notifications) {
        this.notifications = notifications;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_notification, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        NotificationModelMed notification = notifications.get(position);
        holder.title.setText(notification.getTitle());
        holder.message.setText(notification.getMessage());
        holder.timeAgo.setText(notification.getTimeAgo());
    }

    @Override
    public int getItemCount() {
        return notifications.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView title, message, timeAgo;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.notificationTitle);
            message = itemView.findViewById(R.id.notificationMessage);
            timeAgo = itemView.findViewById(R.id.notificationTimeAgo);
        }
    }
}