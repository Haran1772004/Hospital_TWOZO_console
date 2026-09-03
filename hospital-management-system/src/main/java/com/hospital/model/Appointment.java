package com.hospital.model;

public class Appointment {

    private int appointmentId;
    private Patient patient;
    private Doctor doctor;
    private String appointmentDate;
    private String appointmentTime;
    private AppointmentStatus status;

    public Appointment() {
    }

    public Appointment(int appointmentId, Patient patient, Doctor doctor,
                        String appointmentDate, String appointmentTime, AppointmentStatus status) {
        this.appointmentId = appointmentId;
        this.patient = patient;
        this.doctor = doctor;
        this.appointmentDate = appointmentDate;
        this.appointmentTime = appointmentTime;
        this.status = status;
    }

    public Appointment(int appointmentId, Patient patient, Doctor doctor,
                       String appointmentDate, String appointmentTime, String status) {
        this(appointmentId, patient, doctor, appointmentDate, appointmentTime,
                status == null ? null : AppointmentStatus.valueOf(status.toUpperCase()));
    }

    public int getAppointmentId() {
        return appointmentId;
    }

    public void setAppointmentId(int appointmentId) {
        this.appointmentId = appointmentId;
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

    public String getAppointmentDate() {
        return appointmentDate;
    }

    public void setAppointmentDate(String appointmentDate) {
        this.appointmentDate = appointmentDate;
    }

    public String getAppointmentTime() {
        return appointmentTime;
    }

    public void setAppointmentTime(String appointmentTime) {
        this.appointmentTime = appointmentTime;
    }

    public AppointmentStatus getStatus() {
        return status;
    }

    public void setStatus(AppointmentStatus status) {
        this.status = status;
    }

    public void setStatus(String status) { 
        this.status = AppointmentStatus.valueOf(status.toUpperCase()); 
    }

    
    public String toString() {
        return "Appointment{" +
                "appointmentId=" + appointmentId +
                ", patient=" + (patient != null ? patient.getName() : null) +
                ", doctor=" + (doctor != null ? doctor.getName() : null) +
                ", appointmentDate='" + appointmentDate + '\'' +
                ", appointmentTime='" + appointmentTime + '\'' +
                ", status='" + status + '\'' +
                '}';
    }
}