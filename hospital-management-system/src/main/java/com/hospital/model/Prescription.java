package com.hospital.model;

public class Prescription {

  private int prescriptionId;
  private int recordId;
  private String medicineName;
  private String dosage;
  private String duration;

  public Prescription() {}

  public Prescription(
      int prescriptionId, int recordId, String medicineName, String dosage, String duration) {
    this.prescriptionId = prescriptionId;
    this.recordId = recordId;
    this.medicineName = medicineName;
    this.dosage = dosage;
    this.duration = duration;
  }

  public int getPrescriptionId() {
    return prescriptionId;
  }

  public void setPrescriptionId(int prescriptionId) {
    this.prescriptionId = prescriptionId;
  }

  public int getRecordId() {
    return recordId;
  }

  public void setRecordId(int recordId) {
    this.recordId = recordId;
  }

  public String getMedicineName() {
    return medicineName;
  }

  public void setMedicineName(String medicineName) {
    this.medicineName = medicineName;
  }

  public String getDosage() {
    return dosage;
  }

  public void setDosage(String dosage) {
    this.dosage = dosage;
  }

  public String getDuration() {
    return duration;
  }

  public void setDuration(String duration) {
    this.duration = duration;
  }

  @Override
  public String toString() {
    return "Prescription{"
        + "prescriptionId="
        + prescriptionId
        + ", recordId="
        + recordId
        + ", medicineName='"
        + medicineName
        + '\''
        + ", dosage='"
        + dosage
        + '\''
        + ", duration='"
        + duration
        + '\''
        + '}';
  }
}
