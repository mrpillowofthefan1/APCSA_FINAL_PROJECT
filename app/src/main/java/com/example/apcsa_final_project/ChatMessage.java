package com.example.apcsa_final_project;

// object for one chat message
public class ChatMessage {
    private String message;
    private boolean isUser;

    // constructor for message
    public ChatMessage(String message, boolean isUser) {
        this.message = message;
        this.isUser = isUser;
    }

    public String getMessage() {
        return message;
    }

    public boolean isUser() {
        return isUser;
    }
}
