package com.hospital.controller;

import com.hospital.dao.*;
import com.hospital.impl.*;
import com.hospital.model.Appointment;
import com.hospital.model.MedicalRecord;
import com.hospital.model.Patient;
import com.hospital.model.Prescription;

import java.util.List;
import java.util.Scanner;

public class DoctorMenu {

    private static final AppointmentDAO appointmentDAO = new AppointmentDAOImpl();
    private static final PatientDAO patientDAO = new PatientDAOImpl();
    private static final MedicalRecordDAO medicalRecordDAO = new MedicalRecordDAOImpl();
    private static final PrescriptionDAO prescriptionDAO = new PrescriptionDAOImpl();

    public static void show(Scanner scanner) {

        boolean back = false;

        while (!back) {

            System.out.println("\n--- DOCTOR MENU ---");
            System.out.println("1. View My Appointments");
            System.out.println("2. View Patient Details");
            System.out.println("3. Create Medical Record (Diagnosis)");
            System.out.println("4. Prescribe Medicine");
            System.out.println("5. View Medical Records for a Patient");
            System.out.println("0. Back to Main Menu");
            System.out.print("Choose option: ");

            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1" -> viewMyAppointments(scanner);
                case "2" -> viewPatientDetails(scanner);
                case "3" -> createMedicalRecord(scanner);
                case "4" -> prescribeMedicine(scanner);
                case "5" -> viewPatientRecords(scanner);
                case "0" -> back = true;
                default -> System.out.println("Invalid choice.");
            }
        }
    }

    private static void viewMyAppointments(Scanner scanner) {
        int doctorId = InputHelper.readInt(scanner, "Your Doctor ID: ");
        List<Appointment> appointments = appointmentDAO.getAppointmentsByDoctor(doctorId);
        if (appointments.isEmpty()) {
            System.out.println("No appointments found.");
        } else {
            appointments.forEach(System.out::println);
        }
    }

    private static void viewPatientDetails(Scanner scanner) {
        int patientId = InputHelper.readInt(scanner, "Patient ID: ");
        Patient patient = patientDAO.getPatientById(patientId);
        System.out.println(patient != null ? patient : "No patient found with that ID.");
    }

    private static void createMedicalRecord(Scanner scanner) {

        int appointmentId = InputHelper.readInt(scanner, "Appointment ID this record is for: ");

        List<Appointment> allAppointments = appointmentDAO.getAllAppointments();
        Appointment appointment = allAppointments.stream()
                .filter(a -> a.getAppointmentId() == appointmentId)
                .findFirst()
                .orElse(null);

        if (appointment == null) {
            System.out.println("No appointment found with that ID.");
            return;
        }

        String diagnosis = InputHelper.readText(scanner, "Diagnosis: ");
        String treatmentNotes = InputHelper.readText(scanner, "Treatment notes: ");
        String recordDate = InputHelper.readText(scanner, "Record date (yyyy-MM-dd): ");

        MedicalRecord record = new MedicalRecord(
                0, appointment, appointment.getPatient(), appointment.getDoctor(),
                diagnosis, treatmentNotes, recordDate
        );
        medicalRecordDAO.createMedicalRecord(record);

        if (record.getRecordId() > 0) {
            System.out.println("Record created with ID: " + record.getRecordId()
                    + " — you can now prescribe medicine against this record ID.");
        }
    }

    private static void prescribeMedicine(Scanner scanner) {
        int recordId = InputHelper.readInt(scanner, "Medical Record ID: ");

        MedicalRecord record = medicalRecordDAO.getRecordById(recordId);
        if (record == null) {
            System.out.println("No medical record found with that ID.");
            return;
        }

        boolean addMore = true;
        while (addMore) {
            String medicineName = InputHelper.readText(scanner, "Medicine name: ");
            String dosage = InputHelper.readText(scanner, "Dosage (e.g. 500mg): ");
            String duration = InputHelper.readText(scanner, "Duration (e.g. 7 days): ");

            prescriptionDAO.addPrescription(new Prescription(0, recordId, medicineName, dosage, duration));

            String more = InputHelper.readText(scanner, "Add another medicine? (y/n): ");
            addMore = more.equalsIgnoreCase("y");
        }
    }

    private static void viewPatientRecords(Scanner scanner) {
        int patientId = InputHelper.readInt(scanner, "Patient ID: ");
        List<MedicalRecord> records = medicalRecordDAO.getRecordsByPatient(patientId);
        if (records.isEmpty()) {
            System.out.println("No medical records found.");
        } else {
            records.forEach(System.out::println);
        }
    }
}