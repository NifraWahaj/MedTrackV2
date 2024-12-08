package com.example.medtrack.models;

//not used by community Notificaitons
public class NotificationModel {
    // Private fields for encapsulation
    private String userName;
    private String commentText;
    private String timeAgo;

    // Default constructor (no-args constructor)
    public NotificationModel() {
    }

    // Parameterized constructor
    public NotificationModel(String userName, String commentText, String timeAgo) {
        this.userName = userName;
        this.commentText = commentText;
        this.timeAgo = timeAgo;
    }

    // Getter for userName
    public String getUserName() {
        return userName;
    }

    // Setter for userName
    public void setUserName(String userName) {
        this.userName = userName;
    }

    // Getter for commentText
    public String getCommentText() {
        return commentText;
    }

    // Setter for commentText
    public void setCommentText(String commentText) {
        this.commentText = commentText;
    }

    // Getter for timeAgo
    public String getTimeAgo() {
        return timeAgo;
    }

    // Setter for timeAgo
    public void setTimeAgo(String timeAgo) {
        this.timeAgo = timeAgo;
    }

    // toString method for debugging and logging
    @Override
    public String toString() {
        return "NotificationModel{" +
                "userName='" + userName + '\'' +
                ", commentText='" + commentText + '\'' +
                ", timeAgo='" + timeAgo + '\'' +
                '}';
    }
}
