package com.example.administrator.chatapp;

public class Chating {
    public String email;
    public String text;

    public Chating() {
        // Default constructor required for calls to DataSnapshot.getValue(Comment.class)
    }

    public Chating(String email, String text) {
        this.email = email;
        this.text = text;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
