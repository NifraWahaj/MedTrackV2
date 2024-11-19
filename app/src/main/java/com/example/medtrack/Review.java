package com.example.medtrack;public class Review {
    private String userName;
    private float rating;
    private String reviewText;
    private String blogTitle;
    private String userEmail;

    public Review(String userName, float rating, String reviewText, String blogTitle, String userEmail) {
        this.userName = userName;
        this.rating = rating;
        this.reviewText = reviewText;
        this.blogTitle = blogTitle;
        this.userEmail = userEmail;
    }

    // Getters and setters
    public String getUserName() { return userName; }
    public float getRating() { return rating; }
    public String getReviewText() { return reviewText; }
    public String getBlogTitle() { return blogTitle; }
    public String getUserEmail() { return userEmail; }
}
