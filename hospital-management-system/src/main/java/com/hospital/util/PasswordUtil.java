package com.hospital.util;

import org.mindrot.jbcrypt.BCrypt;

public final class PasswordUtil {

  private PasswordUtil() {}

  public static String hashPassword(String rawPassword) {
    if (rawPassword == null) {
      throw new IllegalArgumentException("Password cannot be null");
    }
    return BCrypt.hashpw(rawPassword, BCrypt.gensalt(12));
  }

  public static boolean matches(String rawPassword, String hashedPassword) {
    if (rawPassword == null || hashedPassword == null || hashedPassword.isBlank()) {
      return false;
    }
    try {
      return BCrypt.checkpw(rawPassword, hashedPassword);
    } catch (IllegalArgumentException exception) {
      return false;
    }
  }
}
