package com.example.medtrack.models;


import java.util.concurrent.TimeUnit;

public class NotificationModelMed {
    private String title;
    private String message;
    private long timestamp;

    public NotificationModelMed() {
    }

    public NotificationModelMed(String title, String message, long timestamp) {
        this.title = title;
        this.message = message;
        this.timestamp = timestamp;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getTimeAgo() {
        long currentTime = System.currentTimeMillis();
        long diff = currentTime - timestamp;

        if (diff < TimeUnit.MINUTES.toMillis(1)) {
            return "Just now";
        } else if (diff < TimeUnit.HOURS.toMillis(1)) {
            return diff / TimeUnit.MINUTES.toMillis(1) + "m ago";
        } else if (diff < TimeUnit.DAYS.toMillis(1)) {
            return diff / TimeUnit.HOURS.toMillis(1) + "h ago";
        } else {
            return diff / TimeUnit.DAYS.toMillis(1) + "d ago";
        }
    }
}