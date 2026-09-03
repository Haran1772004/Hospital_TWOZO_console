package com.hospital.localfunctions;

import com.hospital.model.User;
import java.util.List;

public interface UserLF {
    void addUser(User user);
    User getUserByUsername(String username);
    boolean existsByUsername(String username);
    List<User> getAllUsers();
    List<User> getPendingUsersByRole(String role);
    void updateStatus(String username, com.hospital.model.AccountStatus status);
    void removeUser(String username);
}