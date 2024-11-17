package com.example.medtrack;

public class Blog {
    private String id;
    private String title;
    private String content;
    private boolean isApproved;

    public Blog(String id, String title, String content,boolean isApproved) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.isApproved=isApproved;
    }

    public boolean isApproved() {
        return isApproved;
    }

    public void setApproved(boolean approved) {
        isApproved = approved;
    }

    public Blog() {
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setContent(String content) {
        this.content = content;
    }

    // Getters
    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }
}
