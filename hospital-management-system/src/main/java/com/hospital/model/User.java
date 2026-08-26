package com.hospital.model;

public class User {
    private String username;
    private String password;
    private String role;
    private int linkedId;

    public User() { }

    public User(String username, String password, String role, int linkedId) {
        this.username = username;
        this.password = password;
        this.role = role;
        this.linkedId = linkedId;
    }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
    public int getLinkedId() { return linkedId; }
    public void setLinkedId(int linkedId) { this.linkedId = linkedId; }
}
