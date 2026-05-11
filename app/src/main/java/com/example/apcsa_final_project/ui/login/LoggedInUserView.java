package com.example.apcsa_final_project.ui.login;

/**
 * Class exposing authenticated user details to the UI.
 */
class LoggedInUserView {
    private final String displayName;
    private final String dashboardTitle;
    private final String role;

    LoggedInUserView(String displayName, String dashboardTitle, String role) {
        this.displayName = displayName;
        this.dashboardTitle = dashboardTitle;
        this.role = role;
    }

    String getDisplayName() {
        return displayName;
    }

    String getDashboardTitle() {
        return dashboardTitle;
    }

    String getRole() {
        return role;
    }
}
