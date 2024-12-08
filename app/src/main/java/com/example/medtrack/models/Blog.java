package com.example.medtrack.models;

import java.util.HashMap;
import java.util.Map;

public class Blog {
    private String id, userId;
    private String title;
    private String content;

    public void setId(String id) {
        this.id = id;
    }

    private boolean isApproved;
    private Map<String, Float> ratings; // User ID to Rating
    private Map<String, String> reviews; // User ID to Review

    // Constructor
    public Blog(String id, String userId, String title, String content, boolean isApproved) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.userId = userId;
        this.isApproved = isApproved;
        this.ratings = new HashMap<>();
        this.reviews = new HashMap<>();
    }

    // Getters and Setters
    public Map<String, Float> getRatings() {
        return ratings;
    }

    public void setRatings(Map<String, Float> ratings) {
        this.ratings = ratings;
    }

    public Map<String, String> getReviews() {
        return reviews;
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }

    public boolean isApproved() {
        return isApproved;
    }

    public String getUserId() {
        return userId;
    }

    public void setReviews(Map<String, String> reviews) {
        this.reviews = reviews;
    }
}
