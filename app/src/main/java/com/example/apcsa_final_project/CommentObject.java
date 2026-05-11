package com.example.apcsa_final_project;

public class CommentObject {
    private String username;
    private String role;
    private String content;
    private String createdAt;

    public CommentObject(String username, String role, String content, String createdAt) {
        this.username = username;
        this.role = role;
        this.content = content;
        this.createdAt = createdAt;
    }

    public String getUsername() { return username; }
    public String getRole() { return role; }
    public String getContent() { return content; }
    public String getCreatedAt() { return createdAt; }
}
