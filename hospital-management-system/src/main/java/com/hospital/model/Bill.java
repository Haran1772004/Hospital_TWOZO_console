package com.hospital.model;

import java.math.BigDecimal;

public class Bill {

  private int billId;
  private Patient patient;
  private Appointment appointment;
  private BigDecimal consultationCharge;
  private BigDecimal medicineCharge;
  private BigDecimal otherCharge;
  private BigDecimal totalAmount;
  private String billDate;
  private BillStatus status;

  public Bill() {}

  public Bill(
      int billId,
      Patient patient,
      BigDecimal consultationCharge,
      BigDecimal medicineCharge,
      BigDecimal otherCharge,
      BigDecimal totalAmount,
      String billDate,
      BillStatus status) {
    this.billId = billId;
    this.patient = patient;
    this.consultationCharge = consultationCharge;
    this.medicineCharge = medicineCharge;
    this.otherCharge = otherCharge;
    this.totalAmount = totalAmount;
    this.billDate = billDate;
    this.status = status;
  }

  public Bill(
      int billId,
      Appointment appointment,
      BigDecimal consultationCharge,
      BigDecimal medicineCharge,
      BigDecimal otherCharge,
      BigDecimal totalAmount,
      String billDate,
      BillStatus status) {
    this(
        billId,
        appointment == null ? null : appointment.getPatient(),
        consultationCharge,
        medicineCharge,
        otherCharge,
        totalAmount,
        billDate,
        status);
    setAppointment(appointment);
  }

  public Bill(
      int billId,
      Patient patient,
      BigDecimal consultationCharge,
      BigDecimal medicineCharge,
      BigDecimal otherCharge,
      BigDecimal totalAmount,
      String billDate,
      String status) {
    this(
        billId,
        patient,
        consultationCharge,
        medicineCharge,
        otherCharge,
        totalAmount,
        billDate,
        status == null ? null : BillStatus.valueOf(status.toUpperCase()));
  }

  public int getBillId() {
    return billId;
  }

  public void setBillId(int billId) {
    this.billId = billId;
  }

  public Patient getPatient() {
    return patient;
  }

  public Appointment getAppointment() {
    return appointment;
  }

  public void setAppointment(Appointment appointment) {
    this.appointment = appointment;
    this.patient = appointment == null ? null : appointment.getPatient();
  }

  public void setPatient(Patient patient) {
    this.patient = patient;
  }

  public BigDecimal getConsultationCharge() {
    return consultationCharge;
  }

  public void setConsultationCharge(BigDecimal consultationCharge) {
    this.consultationCharge = consultationCharge;
  }

  public BigDecimal getMedicineCharge() {
    return medicineCharge;
  }

  public void setMedicineCharge(BigDecimal medicineCharge) {
    this.medicineCharge = medicineCharge;
  }

  public BigDecimal getOtherCharge() {
    return otherCharge;
  }

  public void setOtherCharge(BigDecimal otherCharge) {
    this.otherCharge = otherCharge;
  }

  public BigDecimal getTotalAmount() {
    return totalAmount;
  }

  public void setTotalAmount(BigDecimal totalAmount) {
    this.totalAmount = totalAmount;
  }

  public String getBillDate() {
    return billDate;
  }

  public void setBillDate(String billDate) {
    this.billDate = billDate;
  }

  public BillStatus getStatus() {
    return status;
  }

  public void setStatus(BillStatus status) {
    this.status = status;
  }

  public void setStatus(String status) {
    this.status = BillStatus.valueOf(status.toUpperCase());
  }

  @Override
  public String toString() {
    return "Bill{"
        + "billId="
        + billId
        + ", patient="
        + (patient != null ? patient.getName() : null)
        + ", consultationCharge="
        + consultationCharge
        + ", medicineCharge="
        + medicineCharge
        + ", otherCharge="
        + otherCharge
        + ", totalAmount="
        + totalAmount
        + ", billDate='"
        + billDate
        + '\''
        + ", status='"
        + status
        + '\''
        + '}';
  }
}
