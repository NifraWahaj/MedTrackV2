package com.example.medtrack.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.medtrack.models.NotificationModel;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.medtrack.R;
import com.example.medtrack.models.Medication;

import java.util.List;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.CommentViewHolder> {

    private List<NotificationModel> notifications;

    // Constructor to initialize the list of notifications
    public NotificationAdapter(List<NotificationModel> notifications) {
        this.notifications = notifications;
    }

    // ViewHolder class to hold references to the views for each item
    public static class CommentViewHolder extends RecyclerView.ViewHolder {
        TextView userName;
        TextView commentText;
        TextView timeAgo;

        public CommentViewHolder(@NonNull View itemView) {
            super(itemView);
            userName = itemView.findViewById(R.id.userName);
            commentText = itemView.findViewById(R.id.commentText);
            timeAgo = itemView.findViewById(R.id.timeAgo);
        }
    }

    @NonNull
    @Override
    public CommentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate the item layout
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_item_notification, parent, false);
        return new CommentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CommentViewHolder holder, int position) {
        // Get the notification item at the current position
        NotificationModel notification = notifications.get(position);

        // Bind data to the views
        holder.userName.setText(notification.getUserName());
        holder.commentText.setText(notification.getCommentText());
        holder.timeAgo.setText(notification.getTimeAgo());
    }

    @Override
    public int getItemCount() {
        // Return the size of the notification list
        return notifications.size();
    }
}
