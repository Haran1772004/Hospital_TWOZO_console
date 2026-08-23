package com.hospital.controller;

import com.hospital.service.PatientService;

import java.util.Scanner;

public class PatientMenu {

    private static final PatientService patientService = new PatientService();

    public static void show(Scanner scanner) {

        int patientId = InputHelper.readInt(scanner, "Enter your Patient ID to log in: ");

        if (patientService.viewPersonalDetails(patientId) == null) {
            System.out.println("No patient found with that ID.");
            return;
        }

        boolean back = false;

        while (!back) {

            System.out.println("\n--- PATIENT MENU ---");
            System.out.println("1. View Personal Details");
            System.out.println("2. View Appointments");
            System.out.println("3. View Medical Records");
            System.out.println("4. View Prescriptions");
            System.out.println("5. View Full Profile (everything)");
            System.out.println("0. Back to Main Menu");
            System.out.print("Choose option: ");

            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1" -> System.out.println(patientService.viewPersonalDetails(patientId));
                case "2" -> patientService.viewAppointments(patientId).forEach(System.out::println);
                case "3" -> patientService.viewMedicalRecords(patientId).forEach(System.out::println);
                case "4" -> patientService.viewPrescriptions(patientId).forEach(System.out::println);
                case "5" -> patientService.viewFullProfile(patientId);
                case "0" -> back = true;
                default -> System.out.println("Invalid choice.");
            }
        }
    }
}