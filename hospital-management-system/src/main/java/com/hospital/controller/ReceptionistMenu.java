package com.hospital.controller;

import com.hospital.dao.*;
import com.hospital.impl.*;
import com.hospital.model.Appointment;
import com.hospital.model.Doctor;
import com.hospital.model.Patient;

import java.util.List;
import java.util.Scanner;

public class ReceptionistMenu {

    private static final PatientDAO patientDAO = new PatientDAOImpl();
    private static final DoctorDAO doctorDAO = new DoctorDAOImpl();
    private static final AppointmentDAO appointmentDAO = new AppointmentDAOImpl();

    public static void show(Scanner scanner) {

        boolean back = false;

        while (!back) {

            System.out.println("\n--- RECEPTIONIST MENU ---");
            System.out.println("1. Register Patient");
            System.out.println("2. Update Patient Details");
            System.out.println("3. Book Appointment");
            System.out.println("4. Cancel Appointment");
            System.out.println("5. View Today's Appointments");
            System.out.println("6. View Patient's Appointments");
            System.out.println("0. Back to Main Menu");
            System.out.print("Choose option: ");

            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1" -> registerPatient(scanner);
                case "2" -> updatePatient(scanner);
                case "3" -> bookAppointment(scanner);
                case "4" -> cancelAppointment(scanner);
                case "5" -> appointmentDAO.getTodaysAppointments().forEach(System.out::println);
                case "6" -> viewPatientAppointments(scanner);
                case "0" -> back = true;
                default -> System.out.println("Invalid choice.");
            }
        }
    }

    private static void registerPatient(Scanner scanner) {
        String name = InputHelper.readText(scanner, "Name: ");
        String dob = InputHelper.readText(scanner, "DOB (yyyy-MM-dd): ");
        String gender = InputHelper.readText(scanner, "Gender: ");
        String phone = InputHelper.readText(scanner, "Phone: ");
        String email = InputHelper.readText(scanner, "Email: ");
        String address = InputHelper.readText(scanner, "Address: ");

        Patient patient = new Patient(0, name, dob, gender, phone, email, address, "ACTIVE");
        patientDAO.addPatient(patient);
    }

    private static void updatePatient(Scanner scanner) {
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

    private static void bookAppointment(Scanner scanner) {

        System.out.println("\nAvailable patients:");
        patientDAO.getAllPatients().forEach(System.out::println);
        int patientId = InputHelper.readInt(scanner, "Patient ID: ");
        Patient patient = patientDAO.getPatientById(patientId);
        if (patient == null) {
            System.out.println("Invalid patient ID.");
            return;
        }

        System.out.println("\nAvailable doctors:");
        doctorDAO.getAllDoctors().forEach(System.out::println);
        int doctorId = InputHelper.readInt(scanner, "Doctor ID: ");
        Doctor doctor = doctorDAO.getDoctorById(doctorId);
        if (doctor == null) {
            System.out.println("Invalid doctor ID.");
            return;
        }

        String date = InputHelper.readText(scanner, "Appointment date (yyyy-MM-dd): ");
        String time = InputHelper.readText(scanner, "Appointment time (HH:mm:ss): ");

        Appointment appointment = new Appointment(0, patient, doctor, date, time, "SCHEDULED");
        appointmentDAO.bookAppointment(appointment);
    }

    private static void cancelAppointment(Scanner scanner) {
        int id = InputHelper.readInt(scanner, "Appointment ID to cancel: ");
        appointmentDAO.cancelAppointment(id);
    }

    private static void viewPatientAppointments(Scanner scanner) {
        int patientId = InputHelper.readInt(scanner, "Patient ID: ");
        List<Appointment> appointments = appointmentDAO.getAppointmentsByPatient(patientId);
        if (appointments.isEmpty()) {
            System.out.println("No appointments found for this patient.");
        } else {
            appointments.forEach(System.out::println);
        }
    }
}