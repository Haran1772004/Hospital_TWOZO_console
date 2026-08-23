package com.hospital.service;

import com.hospital.dao.*;
import com.hospital.impl.*;
import com.hospital.model.*;

import java.util.List;

public class PatientService {

    private final PatientDAO patientDAO = new PatientDAOImpl();
    private final AppointmentDAO appointmentDAO = new AppointmentDAOImpl();
    private final MedicalRecordDAO medicalRecordDAO = new MedicalRecordDAOImpl();
    private final PrescriptionDAO prescriptionDAO = new PrescriptionDAOImpl();

    // 1. VIEW PERSONAL DETAILS
    public Patient viewPersonalDetails(int patientId) {
        return patientDAO.getPatientById(patientId);
    }

    // 2. VIEW APPOINTMENTS (upcoming + past, all in one list, ordered by date)
    public List<Appointment> viewAppointments(int patientId) {
        return appointmentDAO.getAppointmentsByPatient(patientId);
    }

    // 3. VIEW MEDICAL RECORDS
    public List<MedicalRecord> viewMedicalRecords(int patientId) {
        return medicalRecordDAO.getRecordsByPatient(patientId);
    }

    // 4. VIEW PRESCRIPTIONS (across ALL of the patient's medical records)
    public List<Prescription> viewPrescriptions(int patientId) {

        List<Prescription> allPrescriptions = new java.util.ArrayList<>();

        List<MedicalRecord> records = medicalRecordDAO.getRecordsByPatient(patientId);

        for (MedicalRecord record : records) {
            List<Prescription> prescriptionsForRecord =
                    prescriptionDAO.getPrescriptionsByRecord(record.getRecordId());
            allPrescriptions.addAll(prescriptionsForRecord);
        }

        return allPrescriptions;
    }

    // Convenience: print everything for one patient in one go
    public void viewFullProfile(int patientId) {

        System.out.println("========== PATIENT PROFILE ==========");

        Patient patient = viewPersonalDetails(patientId);
        System.out.println("\n--- Personal Details ---");
        System.out.println(patient);

        if (patient == null) {
            System.out.println("No such patient found.");
            return;
        }

        System.out.println("\n--- Appointments ---");
        viewAppointments(patientId).forEach(System.out::println);

        System.out.println("\n--- Medical Records ---");
        viewMedicalRecords(patientId).forEach(System.out::println);

        System.out.println("\n--- Prescriptions ---");
        viewPrescriptions(patientId).forEach(System.out::println);

        System.out.println("\n=======================================");
    }
}