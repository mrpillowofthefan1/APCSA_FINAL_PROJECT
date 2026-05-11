package com.example.apcsa_final_project.data.model;

// base class for users
public abstract class LoggedInUser {

    private final String userId;
    private final String displayName;
    private final String role;

    public LoggedInUser(String userId, String displayName, String role) {
        this.userId = userId;
        this.displayName = displayName;
        this.role = role;
    }

    public String getUserId() {
        return userId;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getRole() {
        return role;
    }

    // polymorphic method for different titles
    public abstract String getDashboardTitle();
}
