package com.example.medtrack;

public class Blog {
    private String id;
    private String title;
    private String content;

    public Blog(String id, String title, String content) {
        this.id = id;
        this.title = title;
        this.content = content;
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
