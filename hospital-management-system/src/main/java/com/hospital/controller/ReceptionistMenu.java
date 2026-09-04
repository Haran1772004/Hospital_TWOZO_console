package com.hospital.controller;

import com.hospital.exception.HospitalException;
import com.hospital.localfunctions.*;
import com.hospital.impl.*;
import com.hospital.model.Appointment;
import com.hospital.model.Doctor;
import com.hospital.model.Patient;
import com.hospital.model.AccountStatus;
import com.hospital.model.AppointmentStatus;
import com.hospital.model.User;
import com.hospital.localfunctions.UserLF;
import com.hospital.util.TablePrinter;
import com.hospital.util.ValidationUtil;

import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

public class ReceptionistMenu {

    private static final PatientLF patientLF = new PatientLFImpl();
    private static final DoctorLF doctorLF = new DoctorLFImpl();
    private static final AppointmentLF appointmentLF = new AppointmentLFImpl();
    private static final UserLF userLF = new UserLFImpl();

    public static void show(Scanner scanner) {

        boolean back = false;

        while (!back) {

            System.out.println("\n--- RECEPTIONIST MENU ---");
            System.out.println("1.  Review Pending Patient Registrations");
            System.out.println("2.  Update Patient Details");
            System.out.println("3.  Deactivate Patient");
            System.out.println("4.  Activate Patient");
            System.out.println("5.  Book Appointment");
            System.out.println("6.  Cancel Appointment");
            System.out.println("7.  View Today's Appointments");
            System.out.println("8.  View Patient's Appointments");
            System.out.println("9.  View All Patients");
            System.out.println("10. View All Appointments");
            System.out.println("0.  Back to Main Menu");
            System.out.print("Choose option: ");

            String choice = scanner.nextLine().trim();

            try {
                switch (choice) {
                case "1" -> reviewPendingPatients(scanner);
                case "2" -> updatePatient(scanner);
                case "3" -> deactivatePatient(scanner);
                case "4" -> activatePatient(scanner);
                case "5" -> bookAppointment(scanner);
                case "6" -> cancelAppointment(scanner);
                case "7" -> TablePrinter.printAppointments(appointmentLF.getTodaysAppointments());
                case "8" -> viewPatientAppointments(scanner);
                case "9" -> TablePrinter.printPatients(patientLF.getAllPatients().stream()
                    .filter(patient -> AccountStatus.ACTIVE == patient.getStatus()).collect(Collectors.toList()));
                case "10" -> TablePrinter.printAppointments(appointmentLF.getAllAppointments());
                case "0" -> back = true;
                default -> System.out.println("Invalid choice.");
                }
            } catch (HospitalException | IllegalArgumentException exception) {
                System.out.println("Operation failed: " + exception.getMessage());
            }
        }
    }

    private static void reviewPendingPatients(Scanner scanner) {
        List<User> pending = userLF.getPendingUsersByRole("PATIENT");
        if (pending.isEmpty()) {
            System.out.println("No pending patient registrations.");
            return;
        }
        for (User user : pending) {
            Patient patient = patientLF.getPatientById(user.getLinkedId());
            System.out.println("Username: " + user.getUsername() + ", Patient: " + (patient == null ? "N/A" : patient.getName()));
            String action = InputHelper.readText(scanner, "Approve (A) or reject (R): ");
            if ("A".equalsIgnoreCase(action)) {
                userLF.updateStatus(user.getUsername(), AccountStatus.ACTIVE);
                if (patient != null) {
                    patient.setStatus(AccountStatus.ACTIVE);
                }
                System.out.println("Patient approved.");
            } else if ("R".equalsIgnoreCase(action)) {
                userLF.updateStatus(user.getUsername(), AccountStatus.REJECTED);
                if (patient != null) {
                    patientLF.removePatient(patient.getPatientId());
                }
                System.out.println("Patient rejected and removed.");
            } else {
                System.out.println("Invalid action; left pending.");
            }
        }
    }

    private static void updatePatient(Scanner scanner) {
        TablePrinter.printPatients(patientLF.getAllPatients());
        int id = InputHelper.readInt(scanner, "Patient ID to update: ");

        Patient existing = patientLF.getPatientById(id);
        if (existing == null) {
            System.out.println("No patient found with ID: " + id);
            return;
        }

        String phone = InputHelper.readText(scanner, "New phone (blank to keep current): ");
        if (!phone.isBlank()) {
            if (!ValidationUtil.isValidPhone(phone)) {
                System.out.println("Invalid phone number. Update cancelled.");
                return;
            }
            if (patientLF.getAllPatients().stream().anyMatch(p -> p.getPatientId() != existing.getPatientId()
                    && p.getPhone().replaceAll("[^0-9]", "").equals(phone.replaceAll("[^0-9]", "")))) {
                System.out.println("A patient with this phone already exists. Update cancelled.");
                return;
            }
            existing.setPhone(phone);
        }

        String email = InputHelper.readText(scanner, "New email (blank to keep current): ");
        if (!email.isBlank()) {
            if (!ValidationUtil.isValidEmail(email)) {
                System.out.println("Invalid email format. Update cancelled.");
                return;
            }
            if (patientLF.getAllPatients().stream().anyMatch(p -> p.getPatientId() != existing.getPatientId()
                    && p.getEmail().trim().equalsIgnoreCase(email.trim()))) {
                System.out.println("A patient with this email already exists. Update cancelled.");
                return;
            }
            existing.setEmail(email);
        }

        String address = InputHelper.readText(scanner, "New address (blank to keep current): ");
        if (!address.isBlank()) {
            existing.setAddress(address);
        }

        patientLF.updatePatient(existing);
    }

    private static void deactivatePatient(Scanner scanner) {
        TablePrinter.printPatients(patientLF.getAllPatients());
        int id = InputHelper.readInt(scanner, "Patient ID to deactivate: ");
        patientLF.deactivatePatient(id);
    }

    private static void activatePatient(Scanner scanner) {
        TablePrinter.printPatients(patientLF.getAllPatients());
        int id = InputHelper.readInt(scanner, "Patient ID to activate: ");
        patientLF.activatePatient(id);
    }

    private static void bookAppointment(Scanner scanner) {

        System.out.println("\nAvailable patients (ACTIVE only):");
        List<Patient> activePatients = patientLF.getAllPatients().stream()
                .filter(p -> AccountStatus.ACTIVE == p.getStatus())
                .collect(Collectors.toList());
        TablePrinter.printPatients(activePatients);

        int patientId = InputHelper.readInt(scanner, "Patient ID: ");
        Patient patient = patientLF.getPatientById(patientId);
        if (patient == null) {
            System.out.println("Invalid patient ID.");
            return;
        }

        if (AccountStatus.ACTIVE != patient.getStatus()) {
            System.out.println("Cannot book an appointment for an inactive patient.");
            return;
        }

        System.out.println("\nAvailable doctors (ACTIVE only):");
        List<Doctor> activeDoctors = doctorLF.getAllDoctors().stream()
                .filter(d -> AccountStatus.ACTIVE == d.getStatus())
                .collect(Collectors.toList());
        TablePrinter.printDoctors(activeDoctors);

        int doctorId = InputHelper.readInt(scanner, "Doctor ID: ");
        Doctor doctor = doctorLF.getDoctorById(doctorId);
        if (doctor == null) {
            System.out.println("Invalid doctor ID.");
            return;
        }

        if (AccountStatus.ACTIVE != doctor.getStatus()) {
            System.out.println("Cannot book an appointment with an inactive doctor.");
            return;
        }

        String date = "";
        while (true) {
            date = InputHelper.readText(scanner, "Appointment date (yyyy-MM-dd): ");
            if (!ValidationUtil.isValidDate(date)) {
                System.out.println("Invalid date format. Use yyyy-MM-dd (e.g. 2025-12-31). Try again.");
                continue;
            }
            break;
        }

        String time = "";
        while (true) {
            time = InputHelper.readText(scanner, "Appointment time (h:mm a, e.g. 2:30 PM): ");
            if (!ValidationUtil.isValidTime(time)) {
                System.out.println("Invalid time format. Use h:mm a, e.g. 2:30 PM or 9:15 AM. Try again.");
                continue;
            }
            break;
        }

        Appointment appointment = new Appointment(0, patient, doctor, date, time, AppointmentStatus.SCHEDULED);
        appointmentLF.bookAppointment(appointment);
    }

    private static void cancelAppointment(Scanner scanner) {
        System.out.println("\nAll appointments:");
        TablePrinter.printAppointments(appointmentLF.getAllAppointments());
        int id = InputHelper.readInt(scanner, "Appointment ID to cancel: ");
        appointmentLF.cancelAppointment(id);
    }

    private static void viewPatientAppointments(Scanner scanner) {
        System.out.println("\nAll patients:");
        TablePrinter.printPatients(patientLF.getAllPatients().stream()
            .filter(patient -> AccountStatus.ACTIVE == patient.getStatus()).collect(Collectors.toList()));
        int patientId = InputHelper.readInt(scanner, "Patient ID: ");
        List<Appointment> appointments = appointmentLF.getAppointmentsByPatient(patientId);
        TablePrinter.printAppointments(appointments);
    }
}
