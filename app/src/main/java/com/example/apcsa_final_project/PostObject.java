package com.example.apcsa_final_project;

// object for a forum thread
public class PostObject {
    private int id;
    private String username;
    private String role;
    private String title;
    private String createdAt;

    public PostObject(int id, String username, String role, String title, String createdAt) {
        this.id = id;
        this.username = username;
        this.role = role;
        this.title = title;
        this.createdAt = createdAt;
    }

    public int getId() { return id; }
    public String getUsername() { return username; }
    public String getRole() { return role; }
    public String getTitle() { return title; }
    public String getCreatedAt() { return createdAt; }
}
