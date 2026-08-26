package com.hospital.util;

import com.hospital.model.User;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class UserStore {
    private static final Map<String, User> users = new ConcurrentHashMap<>();
    static {
        addUser(new User("admin", "admin123", "ADMIN", 0));
        addUser(new User("receptionist", "reception123", "RECEPTIONIST", 0));
        addUser(new User("billing", "billing123", "BILLING", 0));
    }
    private UserStore() { }
    public static void addUser(User user) { users.put(user.getUsername(), user); }
    public static User getUser(String username) { return users.get(username); }
    /** Returns all users in the store. For admin/demo use only. */
    public static List<User> getAllUsers() { return new ArrayList<>(users.values()); }
}
