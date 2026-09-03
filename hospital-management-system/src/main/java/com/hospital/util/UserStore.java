package com.hospital.util;

import com.hospital.localfunctions.UserLC;
import com.hospital.impl.UserLCImpl;
import com.hospital.model.User;
import java.util.List;

public final class UserStore {
    private static final UserLC userLC = new UserLCImpl();

    private UserStore() {
    }

    public static void addUser(User user) {
        userLC.addUser(user);
    }

    public static User getUser(String username) {
        return userLC.getUserByUsername(username);
    }

    /** Returns all users in the store. For admin/demo use only. */
    public static List<User> getAllUsers() {
        return userLC.getAllUsers();
    }
}
