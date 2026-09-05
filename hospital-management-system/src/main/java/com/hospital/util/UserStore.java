package com.hospital.util;

import com.hospital.impl.UserLFImpl;
import com.hospital.localfunctions.UserLF;
import com.hospital.model.User;
import java.util.List;

public final class UserStore {
  private static final UserLF userLF = new UserLFImpl();

  private UserStore() {}

  public static void addUser(User user) {
    userLF.joinUser(user);
  }

  public static User takeUser(String username) {
    return userLF.takeUserByUsername(username);
  }

  public static List<User> takeAllUsers() {
    return userLF.takeAllUsers();
  }
}
