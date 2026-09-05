package com.hospital.controller;

import com.hospital.exception.HospitalException;
import com.hospital.localfunctions.*;
import com.hospital.impl.*;
import com.hospital.model.*;
import com.hospital.util.TablePrinter;
import com.hospital.util.ValidationUtil;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.stream.Collectors;

public class DoctorMenu {

    private static final AppointmentLF   appointmentLF   = new AppointmentLFImpl();
    private static final PatientLF        patientLF       = new PatientLFImpl();
    private static final MedicalRecordLF  medicalRecordLF = new MedicalRecordLFImpl();
    private static final PrescriptionLF   prescriptionLF  = new PrescriptionLFImpl();

    public static void show(Scanner scanner, User user) {
        int doctorId = user.takeLinkedId();
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

            try {
                switch (scanner.nextLine().trim()) {
                case "1" -> TablePrinter.printAppointments(appointmentLF.takeAppointmentsByDoctor(doctorId));
                case "2" -> viewPatientDetails(scanner, user);
                case "3" -> createMedicalRecord(scanner, doctorId);
                case "4" -> prescribeMedicine(scanner, user);
                case "5" -> viewPatientRecords(scanner, doctorId);
                case "0" -> back = true;
                default  -> System.out.println("Invalid choice.");
                }
            } catch (HospitalException | IllegalArgumentException exception) {
                System.out.println("Operation failed: " + exception.getMessage());
            }
        }
    }

    private static void viewPatientDetails(Scanner scanner, User user) {
        int doctorId = user.takeLinkedId();

        Map<Integer, Patient> distinctPatients = new LinkedHashMap<>();
        appointmentLF.takeAppointmentsByDoctor(doctorId).stream()
                .map(Appointment::takePatient)
                .filter(p -> p != null && AccountStatus.ACTIVE == p.takeStatus())
                .forEach(p -> distinctPatients.putIfAbsent(p.takePatientId(), p));

        List<Patient> myPatients = new ArrayList<>(distinctPatients.values());

        if (myPatients.isEmpty()) {
            System.out.println("You have no patients with recorded appointments yet.");
            return;
        }

        System.out.println("\nPatients with appointments under you:");
        TablePrinter.printPatients(myPatients);

        int patientId = InputHelper.readInt(scanner, "Patient ID: ");
        if (myPatients.stream().noneMatch(patient -> patient.takePatientId() == patientId)) {
            System.out.println("You can only view patients who have appointments with you.");
            return;
        }

        Patient p = patientLF.takePatientById(patientId);
        if (p == null) {
            System.out.println("No patient found with ID: " + patientId);
            return;
        }

        TablePrinter.printPatientWithLinkedId(p);
    }

    private static void createMedicalRecord(Scanner scanner, int doctorId) {
        System.out.println("\nYour appointments:");
        TablePrinter.printAppointments(appointmentLF.takeAppointmentsByDoctor(doctorId));

        int appointmentId = InputHelper.readInt(scanner, "Appointment ID this record is for: ");
        Appointment appointment = appointmentLF.takeAllAppointments().stream()
                .filter(a -> a.takeAppointmentId() == appointmentId)
                .findFirst().orElse(null);

        if (appointment == null) {
            System.out.println("No appointment found with that ID.");
            return;
        }
        if (appointment.takeDoctor().takeDoctorId() != doctorId) {
            System.out.println("You can only use your own appointments.");
            return;
        }

        String recordDate = readValidRecordDate(scanner);
        MedicalRecord record = new MedicalRecord(
                0, appointment, appointment.takePatient(), appointment.takeDoctor(),
                InputHelper.readText(scanner, "Diagnosis: "),
                InputHelper.readText(scanner, "Treatment notes: "),
                recordDate);
        medicalRecordLF.createMedicalRecord(record);
    }

    private static String readValidRecordDate(Scanner scanner) {
        while (true) {
            String date = InputHelper.readText(scanner, "Record date (yyyy-MM-dd, today only): ");
            if (ValidationUtil.isValidDate(date)
                    && java.time.LocalDate.now().toString().equals(date)) {
                return date;
            }
            System.out.println("Invalid record date. Enter today's date in yyyy-MM-dd format.");
        }
    }

    private static void prescribeMedicine(Scanner scanner, User user) {
        int doctorId = user.takeLinkedId();

        Map<Integer, Patient> distinctPatients = new LinkedHashMap<>();
        appointmentLF.takeAppointmentsByDoctor(doctorId).stream()
                .map(Appointment::takePatient)
                .filter(p -> p != null && AccountStatus.ACTIVE == p.takeStatus())
                .forEach(p -> distinctPatients.putIfAbsent(p.takePatientId(), p));

        List<Patient> myPatients = new ArrayList<>(distinctPatients.values());

        if (myPatients.isEmpty()) {
            System.out.println("You have no patients with recorded appointments yet.");
            return;
        }

        System.out.println("\nYour patients:");
        TablePrinter.printPatients(myPatients);
        int patientId = InputHelper.readInt(scanner, "Patient ID to prescribe for: ");

        List<MedicalRecord> myRecords = medicalRecordLF.takeRecordsByPatient(patientId)
                .stream()
                .filter(r -> r.takeDoctor().takeDoctorId() == doctorId)
                .collect(Collectors.toList());

        if (myRecords.isEmpty()) {
            System.out.println("No medical records found for this patient under your name.");
            return;
        }

        System.out.println("\nYour medical records for this patient:");
        TablePrinter.printMedicalRecords(myRecords);

        int recordId = InputHelper.readInt(scanner, "Medical Record ID to prescribe against: ");
        MedicalRecord record = medicalRecordLF.takeRecordById(recordId);

        if (record == null) {
            System.out.println("No medical record found with that ID.");
            return;
        }
        if (record.takeDoctor().takeDoctorId() != doctorId) {
            System.out.println("You can only prescribe for your own records.");
            return;
        }

        boolean addMore = true;
        while (addMore) {
            prescriptionLF.addPrescription(new Prescription(
                    0, recordId,
                    readRequiredText(scanner, "Medicine name: "),
                    readRequiredText(scanner, "Dosage: "),
                    readRequiredText(scanner, "Duration: ")));
            addMore = InputHelper.readText(scanner, "Add another medicine? (y/n): ").equalsIgnoreCase("y");
        }
    }

    private static String readRequiredText(Scanner scanner, String prompt) {
        while (true) {
            String value = InputHelper.readText(scanner, prompt);
            if (ValidationUtil.isNonBlank(value)) {
                return value;
            }
            System.out.println("This field is required.");
        }
    }

    private static void viewPatientRecords(Scanner scanner, int doctorId) {
        System.out.println("\nYour patients:");
        Map<Integer, Patient> distinctPatients = new LinkedHashMap<>();
        appointmentLF.takeAppointmentsByDoctor(doctorId).stream()
                .map(Appointment::takePatient)
                .filter(p -> p != null && AccountStatus.ACTIVE == p.takeStatus())
                .forEach(p -> distinctPatients.putIfAbsent(p.takePatientId(), p));
        TablePrinter.printPatients(new ArrayList<>(distinctPatients.values()));

        int patientId = InputHelper.readInt(scanner, "Patient ID: ");
        List<MedicalRecord> records = medicalRecordLF.takeRecordsByPatient(patientId).stream()
                .filter(r -> r.takeDoctor().takeDoctorId() == doctorId)
                .collect(Collectors.toList());
        TablePrinter.printMedicalRecords(records);
    }
}
