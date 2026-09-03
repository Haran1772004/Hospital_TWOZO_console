package com.hospital.model;

public class MedicalRecord {

    private int recordId;
    private Appointment appointment;
    private Patient patient;
    private Doctor doctor;
    private String diagnosis;
    private String treatmentNotes;
    private String recordDate;

    public MedicalRecord() {
    }

    public MedicalRecord(int recordId, Appointment appointment, Patient patient,
                          Doctor doctor, String diagnosis, String treatmentNotes,
                          String recordDate) {
        this.recordId = recordId;
        this.appointment = appointment;
        this.patient = patient;
        this.doctor = doctor;
        this.diagnosis = diagnosis;
        this.treatmentNotes = treatmentNotes;
        this.recordDate = recordDate;
    }

    public int getRecordId() {
        return recordId;
    }

    public void setRecordId(int recordId) {
        this.recordId = recordId;
    }

    public Appointment getAppointment() {
        return appointment;
    }

    public void setAppointment(Appointment appointment) {
        this.appointment = appointment;
    }

    public Patient getPatient() {
        return patient;
    }

    public void setPatient(Patient patient) {
        this.patient = patient;
    }

    public Doctor getDoctor() {
        return doctor;
    }

    public void setDoctor(Doctor doctor) {
        this.doctor = doctor;
    }

    public String getDiagnosis() {
        return diagnosis;
    }

    public void setDiagnosis(String diagnosis) {
        this.diagnosis = diagnosis;
    }

    public String getTreatmentNotes() {
        return treatmentNotes;
    }

    public void setTreatmentNotes(String treatmentNotes) {
        this.treatmentNotes = treatmentNotes;
    }

    public String getRecordDate() {
        return recordDate;
    }

    public void setRecordDate(String recordDate) {
        this.recordDate = recordDate;
    }

    
    public String toString() {
        return "MedicalRecord{" +
                "recordId=" + recordId +
                ", appointmentId=" + (appointment != null ? appointment.getAppointmentId() : null) +
                ", patient=" + (patient != null ? patient.getName() : null) +
                ", doctor=" + (doctor != null ? doctor.getName() : null) +
                ", diagnosis='" + diagnosis + '\'' +
                ", treatmentNotes='" + treatmentNotes + '\'' +
                ", recordDate='" + recordDate + '\'' +
                '}';
    }
}