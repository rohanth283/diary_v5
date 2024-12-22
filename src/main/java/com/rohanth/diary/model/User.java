package com.rohanth.diary.model;

public class User {
    private Long id;
    private String username;
    private String password;

    // Constructors, getters, and setters
    public User(Long id, String username, String password) {
        this.id = id;
        this.username = username;
        this.password = password;
    }

    public Long getId() { return id; }
    public String getUsername() { return username; }
    public String getPassword() { return password; }
}
