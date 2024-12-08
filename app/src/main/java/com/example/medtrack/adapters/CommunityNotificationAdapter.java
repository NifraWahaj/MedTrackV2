package com.example.medtrack.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.medtrack.R;
import com.example.medtrack.models.Notification;

import java.util.List;

public class CommunityNotificationAdapter extends RecyclerView.Adapter<CommunityNotificationAdapter.CommentViewHolder> {

    private List<Notification> notifications;
    private OnBlogClickListener onBlogClickListener;
    private Context context;

    public interface OnBlogClickListener {
        void onBlogClick(String blogId);
    }

    public CommunityNotificationAdapter(List<Notification> notifications, Context c, OnBlogClickListener listener) {
        this.notifications = notifications;
        this.context = c;
        this.onBlogClickListener = listener;
    }

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
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_item_notification, parent, false);
        return new CommentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CommentViewHolder holder, int position) {
        Notification notification = notifications.get(position);
        Log.d("NotificationAdapter", "Binding item at position: " + position);
        Log.d("NotificationAdapter", "UserName: " + notification.getUserName());
        Log.d("NotificationAdapter", "Comment: " + notification.getCommentText());
        Log.d("NotificationAdapter", "TimeAgo: " + notification.getTimeAgo());

        holder.userName.setText(notification.getUserName());
        holder.commentText.setText(notification.getCommentText());
        holder.timeAgo.setText(notification.getTimeAgo());

        holder.itemView.setOnClickListener(v -> {
            if (onBlogClickListener != null) {
                Toast.makeText(context, "item clicked", Toast.LENGTH_SHORT).show();
                onBlogClickListener.onBlogClick(notification.getBlogId());
            }
        });
    }

    @Override
    public int getItemCount() {
        return notifications.size();
    }

    public void setOnBlogClickListener(OnBlogClickListener listener) {
        this.onBlogClickListener = listener;
    }
}
