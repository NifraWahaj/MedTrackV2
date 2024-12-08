package com.example.medtrack.models;

public class Notification extends NotificationModel {
    private String userName, blogId;
    private String commentText, userId, title;
    private long timestamp;

    // Default constructor required for Firebase
    public Notification() {
    }

    public Notification(String blogId, String commenttext, String title, long timestamp, String userName) {
        this.userName = userName;
        this.commentText = commenttext;
        this.title = title;
        this.blogId = blogId;
        this.timestamp = timestamp;
    }

    public String getBlogId() {
        return blogId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getCommentText() {
        return commentText;
    }

    public void setCommentText(String commentText) {
        this.commentText = commentText;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getTimeAgo() {
        // Convert timestamp to a "time ago" format
        long currentTime = System.currentTimeMillis();
        long diff = currentTime - timestamp;

        if (diff < 60000) {
            return "Just now";
        } else if (diff < 3600000) {
            return diff / 60000 + "m ago";
        } else if (diff < 86400000) {
            return diff / 3600000 + "h ago";
        } else {
            return diff / 86400000 + "d ago";
        }
    }
}
