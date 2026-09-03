package com.hospital.util;

import com.hospital.localfunctions.UserLF;
import com.hospital.impl.UserLFImpl;
import com.hospital.model.User;
import java.util.List;

public final class UserStore {
    private static final UserLF userLF = new UserLFImpl();

    private UserStore() {
    }

    public static void addUser(User user) {
        userLF.addUser(user);
    }

    public static User getUser(String username) {
        return userLF.getUserByUsername(username);
    }

    /** Returns all users in the store. For admin/demo use only. */
    public static List<User> getAllUsers() {
        return userLF.getAllUsers();
    }
}
