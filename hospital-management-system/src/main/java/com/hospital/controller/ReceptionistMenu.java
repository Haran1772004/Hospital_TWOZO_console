package com.hospital.controller;

import com.hospital.dao.*;
import com.hospital.impl.*;
import com.hospital.model.Appointment;
import com.hospital.model.Doctor;
import com.hospital.model.Patient;
import com.hospital.util.TablePrinter;
import com.hospital.util.UserStore;
import com.hospital.model.User;

import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

public class ReceptionistMenu {

    private static final PatientDAO     patientDAO     = new PatientDAOImpl();
    private static final DoctorDAO      doctorDAO      = new DoctorDAOImpl();
    private static final AppointmentDAO appointmentDAO = new AppointmentDAOImpl();

    public static void show(Scanner scanner) {

        boolean back = false;

        while (!back) {

            System.out.println("\n--- RECEPTIONIST MENU ---");
            System.out.println("1.  Register Patient");
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

            switch (choice) {
                case "1"  -> registerPatient(scanner);
                case "2"  -> updatePatient(scanner);
                case "3"  -> deactivatePatient(scanner);
                case "4"  -> activatePatient(scanner);
                case "5"  -> bookAppointment(scanner);
                case "6"  -> cancelAppointment(scanner);
                case "7"  -> TablePrinter.printAppointments(appointmentDAO.getTodaysAppointments());
                case "8"  -> viewPatientAppointments(scanner);
                case "9"  -> TablePrinter.printPatients(patientDAO.getAllPatients());
                case "10" -> TablePrinter.printAppointments(appointmentDAO.getAllAppointments());
                case "0"  -> back = true;
                default   -> System.out.println("Invalid choice.");
            }
        }
    }

    private static void registerPatient(Scanner scanner) {
        String name    = InputHelper.readText(scanner, "Name: ");
        String dob     = InputHelper.readText(scanner, "DOB (yyyy-MM-dd): ");
        String gender  = InputHelper.readText(scanner, "Gender: ");
        String phone   = InputHelper.readText(scanner, "Phone: ");
        String email   = InputHelper.readText(scanner, "Email: ");
        String address = InputHelper.readText(scanner, "Address: ");

        Patient patient = new Patient(0, name, dob, gender, phone, email, address, "ACTIVE");
        patientDAO.addPatient(patient);
        String username = "patient" + patient.getPatientId();
        String password = "patient" + patient.getPatientId() + "123";
        UserStore.addUser(new User(username, password, "PATIENT", patient.getPatientId()));
        System.out.println("Patient login created: " + username + " / " + password);
    }

    private static void updatePatient(Scanner scanner) {
        TablePrinter.printPatients(patientDAO.getAllPatients());
        int id = InputHelper.readInt(scanner, "Patient ID to update: ");

        Patient existing = patientDAO.getPatientById(id);
        if (existing == null) {
            System.out.println("No patient found with ID: " + id);
            return;
        }

        String phone = InputHelper.readText(scanner, "New phone (blank to keep current): ");
        if (!phone.isBlank()) existing.setPhone(phone);

        String email = InputHelper.readText(scanner, "New email (blank to keep current): ");
        if (!email.isBlank()) existing.setEmail(email);

        String address = InputHelper.readText(scanner, "New address (blank to keep current): ");
        if (!address.isBlank()) existing.setAddress(address);

        patientDAO.updatePatient(existing);
    }

    private static void deactivatePatient(Scanner scanner) {
        TablePrinter.printPatients(patientDAO.getAllPatients());
        int id = InputHelper.readInt(scanner, "Patient ID to deactivate: ");
        patientDAO.deactivatePatient(id);
    }

    private static void activatePatient(Scanner scanner) {
        TablePrinter.printPatients(patientDAO.getAllPatients());
        int id = InputHelper.readInt(scanner, "Patient ID to activate: ");
        patientDAO.activatePatient(id);
    }

    private static void bookAppointment(Scanner scanner) {

        // Bug fix: show ACTIVE patients only so inactive ones cannot be selected
        System.out.println("\nAvailable patients (ACTIVE only):");
        List<Patient> activePatients = patientDAO.getAllPatients().stream()
                .filter(p -> "ACTIVE".equals(p.getStatus()))
                .collect(Collectors.toList());
        TablePrinter.printPatients(activePatients);

        int patientId = InputHelper.readInt(scanner, "Patient ID: ");
        Patient patient = patientDAO.getPatientById(patientId);
        if (patient == null) {
            System.out.println("Invalid patient ID.");
            return;
        }

        // Bug fix: guard against booking an inactive patient even if ID is typed manually
        if (!"ACTIVE".equals(patient.getStatus())) {
            System.out.println("Cannot book an appointment for an inactive patient.");
            return;
        }

        // Show ACTIVE doctors only
        System.out.println("\nAvailable doctors (ACTIVE only):");
        List<Doctor> activeDoctors = doctorDAO.getAllDoctors().stream()
                .filter(d -> "ACTIVE".equals(d.getStatus()))
                .collect(Collectors.toList());
        TablePrinter.printDoctors(activeDoctors);

        int doctorId = InputHelper.readInt(scanner, "Doctor ID: ");
        Doctor doctor = doctorDAO.getDoctorById(doctorId);
        if (doctor == null) {
            System.out.println("Invalid doctor ID.");
            return;
        }

        // Guard against booking an inactive doctor even if ID is typed manually
        if (!"ACTIVE".equals(doctor.getStatus())) {
            System.out.println("Cannot book an appointment with an inactive doctor.");
            return;
        }

        String date = InputHelper.readText(scanner, "Appointment date (yyyy-MM-dd): ");
        String time = InputHelper.readText(scanner, "Appointment time (HH:mm:ss): ");

        Appointment appointment = new Appointment(0, patient, doctor, date, time, "SCHEDULED");
        appointmentDAO.bookAppointment(appointment);
    }

    private static void cancelAppointment(Scanner scanner) {
        // UX: show all appointments first so receptionist can see valid IDs
        System.out.println("\nAll appointments:");
        TablePrinter.printAppointments(appointmentDAO.getAllAppointments());
        int id = InputHelper.readInt(scanner, "Appointment ID to cancel: ");
        appointmentDAO.cancelAppointment(id);
    }

    private static void viewPatientAppointments(Scanner scanner) {
        // UX: show patient list first so receptionist can see valid IDs
        System.out.println("\nAll patients:");
        TablePrinter.printPatients(patientDAO.getAllPatients());
        int patientId = InputHelper.readInt(scanner, "Patient ID: ");
        List<Appointment> appointments = appointmentDAO.getAppointmentsByPatient(patientId);
        TablePrinter.printAppointments(appointments);
    }
}
