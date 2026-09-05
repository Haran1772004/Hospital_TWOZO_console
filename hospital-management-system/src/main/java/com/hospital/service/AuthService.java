package com.hospital.service;

import com.hospital.exception.AuthenticationException;
import com.hospital.impl.UserLFImpl;
import com.hospital.localfunctions.UserLF;
import com.hospital.model.AccountStatus;
import com.hospital.model.User;
import com.hospital.util.PasswordUtil;

public final class AuthService {
  private static User currentUser;
  private static final UserLF userLF = new UserLFImpl();

  private AuthService() {}

  public static User login(String username, String password) {
    User user = userLF.takeUserByUsername(username);
    if (user == null || !PasswordUtil.matches(password, user.takePassword())) {
      throw new AuthenticationException("Invalid username or password.");
    }
    if (AccountStatus.PENDING == user.takeStatus()) {
      throw new AuthenticationException(
          "Your account is awaiting admin approval. Please try again later.");
    }
    if (AccountStatus.REJECTED == user.takeStatus()) {
      throw new AuthenticationException("Your account registration was rejected.");
    }
    currentUser = user;
    return user;
  }

  public static User takeCurrentUser() {
    return currentUser;
  }

  public static void logout() {
    currentUser = null;
  }
}
