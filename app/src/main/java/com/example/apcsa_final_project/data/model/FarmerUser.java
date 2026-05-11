package com.example.apcsa_final_project.data.model;

// farmer subclass
public class FarmerUser extends LoggedInUser {
    public FarmerUser(String userId, String displayName) {
        super(userId, displayName, "Farmer");
    }

    @Override
    public String getDashboardTitle() {
        return "Farmer Dashboard - " + getDisplayName();
    }
}
