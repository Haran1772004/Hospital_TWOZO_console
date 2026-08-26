package com.hospital.service;

import com.hospital.exception.AuthenticationException;
import com.hospital.model.User;
import com.hospital.util.UserStore;

public final class AuthService {
    private static User currentUser;
    private AuthService() { }

    public static User login(String username, String password) {
        User user = UserStore.getUser(username);
        if (user == null || !user.getPassword().equals(password)) {
            throw new AuthenticationException("Invalid username or password.");
        }
        currentUser = user;
        return user;
    }
    public static User getCurrentUser() { return currentUser; }
    public static void logout() { currentUser = null; }
}
