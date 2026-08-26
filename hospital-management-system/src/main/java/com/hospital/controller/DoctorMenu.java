package com.hospital.controller;

import com.hospital.dao.*;
import com.hospital.impl.*;
import com.hospital.model.*;
import com.hospital.util.TablePrinter;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.stream.Collectors;

public class DoctorMenu {

    private static final AppointmentDAO   appointmentDAO   = new AppointmentDAOImpl();
    private static final PatientDAO        patientDAO       = new PatientDAOImpl();
    private static final MedicalRecordDAO  medicalRecordDAO = new MedicalRecordDAOImpl();
    private static final PrescriptionDAO   prescriptionDAO  = new PrescriptionDAOImpl();

    public static void show(Scanner scanner, User user) {
        int doctorId = user.getLinkedId();
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

            switch (scanner.nextLine().trim()) {
                case "1" -> TablePrinter.printAppointments(appointmentDAO.getAppointmentsByDoctor(doctorId));
                case "2" -> viewPatientDetails(scanner, user);
                case "3" -> createMedicalRecord(scanner, doctorId);
                case "4" -> prescribeMedicine(scanner, user);
                case "5" -> viewPatientRecords(scanner, doctorId);
                case "0" -> back = true;
                default  -> System.out.println("Invalid choice.");
            }
        }
    }

    /**
     * Shows only patients who have had at least one appointment with this doctor,
     * collected as distinct patients (no duplicates) using a LinkedHashMap keyed by patientId.
     * The selected patient is then displayed including their linkedId (login ID).
     */
    private static void viewPatientDetails(Scanner scanner, User user) {
        int doctorId = user.getLinkedId();

        // Collect distinct patients from this doctor's appointments
        Map<Integer, Patient> distinctPatients = new LinkedHashMap<>();
        appointmentDAO.getAppointmentsByDoctor(doctorId).stream()
                .map(Appointment::getPatient)
                .filter(p -> p != null)
                .forEach(p -> distinctPatients.putIfAbsent(p.getPatientId(), p));

        List<Patient> myPatients = new ArrayList<>(distinctPatients.values());

        if (myPatients.isEmpty()) {
            System.out.println("You have no patients with recorded appointments yet.");
            return;
        }

        System.out.println("\nPatients with appointments under you:");
        TablePrinter.printPatients(myPatients);

        int patientId = InputHelper.readInt(scanner, "Patient ID: ");
        Patient p = patientDAO.getPatientById(patientId);
        if (p == null) {
            System.out.println("No patient found with ID: " + patientId);
            return;
        }

        // Print patient details; linkedId shown so the doctor can refer to login ID if needed
        TablePrinter.printPatientWithLinkedId(p);
    }

    private static void createMedicalRecord(Scanner scanner, int doctorId) {
        System.out.println("\nYour appointments:");
        TablePrinter.printAppointments(appointmentDAO.getAppointmentsByDoctor(doctorId));

        int appointmentId = InputHelper.readInt(scanner, "Appointment ID this record is for: ");
        Appointment appointment = appointmentDAO.getAllAppointments().stream()
                .filter(a -> a.getAppointmentId() == appointmentId)
                .findFirst().orElse(null);

        if (appointment == null) {
            System.out.println("No appointment found with that ID.");
            return;
        }
        if (appointment.getDoctor().getDoctorId() != doctorId) {
            System.out.println("You can only use your own appointments.");
            return;
        }

        MedicalRecord record = new MedicalRecord(
                0, appointment, appointment.getPatient(), appointment.getDoctor(),
                InputHelper.readText(scanner, "Diagnosis: "),
                InputHelper.readText(scanner, "Treatment notes: "),
                InputHelper.readText(scanner, "Record date (yyyy-MM-dd): "));
        medicalRecordDAO.createMedicalRecord(record);
    }

    /**
     * Before prompting for a Record ID, first asks for the patient, then shows only
     * records that belong to BOTH that patient AND the logged-in doctor.
     * This prevents prescribing against another doctor's record.
     */
    private static void prescribeMedicine(Scanner scanner, User user) {
        int doctorId = user.getLinkedId();

        // Step 1: choose the patient from this doctor's own patient list
        Map<Integer, Patient> distinctPatients = new LinkedHashMap<>();
        appointmentDAO.getAppointmentsByDoctor(doctorId).stream()
                .map(Appointment::getPatient)
                .filter(p -> p != null)
                .forEach(p -> distinctPatients.putIfAbsent(p.getPatientId(), p));

        List<Patient> myPatients = new ArrayList<>(distinctPatients.values());

        if (myPatients.isEmpty()) {
            System.out.println("You have no patients with recorded appointments yet.");
            return;
        }

        System.out.println("\nYour patients:");
        TablePrinter.printPatients(myPatients);
        int patientId = InputHelper.readInt(scanner, "Patient ID to prescribe for: ");

        // Step 2: show only this doctor's own records for that patient
        List<MedicalRecord> myRecords = medicalRecordDAO.getRecordsByPatient(patientId)
                .stream()
                .filter(r -> r.getDoctor().getDoctorId() == doctorId)
                .collect(Collectors.toList());

        if (myRecords.isEmpty()) {
            System.out.println("No medical records found for this patient under your name.");
            return;
        }

        System.out.println("\nYour medical records for this patient:");
        TablePrinter.printMedicalRecords(myRecords);

        // Step 3: prescribe against the chosen record
        int recordId = InputHelper.readInt(scanner, "Medical Record ID to prescribe against: ");
        MedicalRecord record = medicalRecordDAO.getRecordById(recordId);

        if (record == null) {
            System.out.println("No medical record found with that ID.");
            return;
        }
        if (record.getDoctor().getDoctorId() != doctorId) {
            System.out.println("You can only prescribe for your own records.");
            return;
        }

        boolean addMore = true;
        while (addMore) {
            prescriptionDAO.addPrescription(new Prescription(
                    0, recordId,
                    InputHelper.readText(scanner, "Medicine name: "),
                    InputHelper.readText(scanner, "Dosage: "),
                    InputHelper.readText(scanner, "Duration: ")));
            addMore = InputHelper.readText(scanner, "Add another medicine? (y/n): ").equalsIgnoreCase("y");
        }
    }

    private static void viewPatientRecords(Scanner scanner, int doctorId) {
        System.out.println("\nYour patients:");
        Map<Integer, Patient> distinctPatients = new LinkedHashMap<>();
        appointmentDAO.getAppointmentsByDoctor(doctorId).stream()
                .map(Appointment::getPatient)
                .filter(p -> p != null)
                .forEach(p -> distinctPatients.putIfAbsent(p.getPatientId(), p));
        TablePrinter.printPatients(new ArrayList<>(distinctPatients.values()));

        int patientId = InputHelper.readInt(scanner, "Patient ID: ");
        List<MedicalRecord> records = medicalRecordDAO.getRecordsByPatient(patientId).stream()
                .filter(r -> r.getDoctor().getDoctorId() == doctorId)
                .collect(Collectors.toList());
        TablePrinter.printMedicalRecords(records);
    }
}
