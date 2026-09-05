package com.hospital.localfunctions;

import com.hospital.model.User;
import java.util.List;

public interface UserLF {
    void joinUser(User user);

    User takeUserByUsername(String username);

    boolean existsByUsername(String username);

    List<User> takeAllUsers();

    List<User> takePendingUsersByRole(String role);

    void updateStatus(String username, com.hospital.model.AccountStatus status);

    void removeUser(String username);
}
