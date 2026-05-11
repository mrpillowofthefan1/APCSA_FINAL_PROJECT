package com.example.apcsa_final_project.data.model;

// normal user subclass
public class NormalUser extends LoggedInUser {
    public NormalUser(String userId, String displayName) {
        super(userId, displayName, "User");
    }

    @Override
    public String getDashboardTitle() {
        return "Customer Dashboard - " + getDisplayName();
    }
}
