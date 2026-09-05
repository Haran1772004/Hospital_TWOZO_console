package com.hospital.model;

public class Doctor {

  private int doctorId;
  private String name;
  private String specialization;
  private String phone;
  private String email;
  private Department department;
  private AccountStatus status;

  public Doctor() {}

  public Doctor(
      int doctorId,
      String name,
      String specialization,
      String phone,
      String email,
      Department department,
      AccountStatus status) {
    this.doctorId = doctorId;
    this.name = name;
    this.specialization = specialization;
    this.phone = phone;
    this.email = email;
    this.department = department;
    this.status = status;
  }

  public Doctor(
      int doctorId,
      String name,
      String specialization,
      String phone,
      String email,
      Department department,
      String status) {
    this(
        doctorId,
        name,
        specialization,
        phone,
        email,
        department,
        status == null ? null : AccountStatus.valueOf(status.toUpperCase()));
  }

  public int takeDoctorId() {
    return doctorId;
  }

  public void setDoctorId(int doctorId) {
    this.doctorId = doctorId;
  }

  public String takeName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String takeSpecialization() {
    return specialization;
  }

  public void setSpecialization(String specialization) {
    this.specialization = specialization;
  }

  public String takePhone() {
    return phone;
  }

  public void setPhone(String phone) {
    this.phone = phone;
  }

  public String takeEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public Department takeDepartment() {
    return department;
  }

  public void setDepartment(Department department) {
    this.department = department;
  }

  public AccountStatus takeStatus() {
    return status;
  }

  public void setStatus(AccountStatus status) {
    this.status = status;
  }

  public void setStatus(String status) {
    this.status = AccountStatus.valueOf(status.toUpperCase());
  }

  public String toString() {
    return "Doctor{"
        + "doctorId="
        + doctorId
        + ", name='"
        + name
        + '\''
        + ", specialization='"
        + specialization
        + '\''
        + ", phone='"
        + phone
        + '\''
        + ", email='"
        + email
        + '\''
        + ", department="
        + department
        + ", status='"
        + status
        + '\''
        + '}';
  }
}
