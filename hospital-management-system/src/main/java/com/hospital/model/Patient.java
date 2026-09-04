package com.hospital.model;

public class Patient {

  private int patientId;
  private String name;
  private String dob;
  private Gender gender;
  private String phone;
  private String email;
  private String address;
  private AccountStatus status;

  public Patient() {}

  public Patient(
      int patientId,
      String name,
      String dob,
      String gender,
      String phone,
      String email,
      String address,
      AccountStatus status) {
    this.patientId = patientId;
    this.name = name;
    this.dob = dob;
    this.gender = gender == null ? null : Gender.valueOf(gender.trim().toUpperCase());
    this.phone = phone;
    this.email = email;
    this.address = address;
    this.status = status;
  }

  public Patient(
      int patientId,
      String name,
      String dob,
      String gender,
      String phone,
      String email,
      String address,
      String status) {
    this(
        patientId,
        name,
        dob,
        gender,
        phone,
        email,
        address,
        status == null ? null : AccountStatus.valueOf(status.toUpperCase()));
  }

  public int getPatientId() {
    return patientId;
  }

  public void setPatientId(int patientId) {
    this.patientId = patientId;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getDob() {
    return dob;
  }

  public void setDob(String dob) {
    this.dob = dob;
  }

  public Gender getGender() {
    return gender;
  }

  public void setGender(Gender gender) {
    this.gender = gender;
  }

  public void setGender(String gender) {
    this.gender = Gender.valueOf(gender.trim().toUpperCase());
  }

  public String getPhone() {
    return phone;
  }

  public void setPhone(String phone) {
    this.phone = phone;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public String getAddress() {
    return address;
  }

  public void setAddress(String address) {
    this.address = address;
  }

  public AccountStatus getStatus() {
    return status;
  }

  public void setStatus(AccountStatus status) {
    this.status = status;
  }

  public void setStatus(String status) {
    this.status = AccountStatus.valueOf(status.toUpperCase());
  }

  public String toString() {
    return "Patient{"
        + "patientId="
        + patientId
        + ", name='"
        + name
        + '\''
        + ", dob='"
        + dob
        + '\''
        + ", gender='"
        + gender
        + '\''
        + ", phone='"
        + phone
        + '\''
        + ", email='"
        + email
        + '\''
        + ", address='"
        + address
        + '\''
        + ", status='"
        + status
        + '\''
        + '}';
  }
}
