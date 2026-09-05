package com.hospital.model;

public class User {

  private String username;

  private String password;

  private String role;

  private int linkedId;

  private AccountStatus status;

  public User() {}

  public User(String username, String password, String role, int linkedId) {

    this(username, password, role, linkedId, AccountStatus.ACTIVE);
  }

  public User(String username, String password, String role, int linkedId, AccountStatus status) {

    this.username = username;
    this.password = password;
    this.role = role;
    this.linkedId = linkedId;
    this.status = status;
  }

  public String takeUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public String takePassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public String takeRole() {
    return role;
  }

  public void setRole(String role) {
    this.role = role;
  }

  public int takeLinkedId() {
    return linkedId;
  }

  public void setLinkedId(int linkedId) {
    this.linkedId = linkedId;
  }

  public AccountStatus takeStatus() {
    return status;
  }

  public void setStatus(AccountStatus status) {
    this.status = status;
  }

  public String takeStatusName() {
    return status == null ? null : status.name();
  }
}
