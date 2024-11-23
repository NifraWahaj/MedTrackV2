package com.example.medtrack.models;
public class Review {
    private String userName;
    private float rating;
    private String reviewText;
    private String userId;
    private String blogTitle;
    private String userEmail;

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserId() {
        return userId;
    }

    public Review(String userId, float rating, String reviewText) {
        this.userId = userId;
        this.rating = rating;
        this.reviewText = reviewText;

    }
public Review(){}
    public void setUserName(String userName) {
        this.userName = userName;
    }
    public void setReviewText(String reviewText) {
        this.reviewText = reviewText;
    }  public void setRating(Float rating) {
        this.rating = rating;
    }

    // Getters and setters
    public String getUserName() { return userName; }
    public float getRating() { return rating; }
    public String getReviewText() { return reviewText; }
    public String getBlogTitle() { return blogTitle; }
    public String getUserEmail() { return userEmail; }
}
