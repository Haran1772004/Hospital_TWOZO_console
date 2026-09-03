package com.hospital.impl;

import com.hospital.localfunctions.UserLC;
import com.hospital.model.User;
import com.hospital.model.AccountStatus;
import com.hospital.util.PasswordUtil;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class UserLCImpl implements UserLC {
    private static final Map<String, User> users = new ConcurrentHashMap<>();

    static {
        users.put("admin", new User("admin", PasswordUtil.hashPassword("admin123"), "ADMIN", 0, AccountStatus.ACTIVE));
       
        users.put("receptionist", new User("receptionist", PasswordUtil.hashPassword("reception123"), "RECEPTIONIST", 0, AccountStatus.ACTIVE));
       
        users.put("billing", new User("billing", PasswordUtil.hashPassword("billing123"), "BILLING", 0, AccountStatus.ACTIVE));
    }

    public void addUser(User user) {

        if (user == null || user.getUsername() == null) {
            throw new IllegalArgumentException("User and username are required");
        }

        if (users.putIfAbsent(user.getUsername().trim().toLowerCase(), user) != null) {
            throw new IllegalArgumentException("Username already exists");
        }

    }

    
    public User getUserByUsername(String username) {
        
        return username == null ? null : users.get(username.trim().toLowerCase());
    }

    
    public boolean existsByUsername(String username) {
        return getUserByUsername(username) != null;
    }

    
    public List<User> getAllUsers() {
        return new ArrayList<>(users.values());
    }

    public List<User> getPendingUsersByRole(String role) {
        return users.values().stream().filter(user -> role.equals(user.getRole())
                && AccountStatus.PENDING == user.getStatus()).toList();
    }

    public void updateStatus(String username, AccountStatus status) {
        User user = getUserByUsername(username);
        if (user == null) throw new IllegalArgumentException("User not found");
        user.setStatus(status);
    }

    public void removeUser(String username) {
        if (username != null) users.remove(username.trim().toLowerCase());
    }
}