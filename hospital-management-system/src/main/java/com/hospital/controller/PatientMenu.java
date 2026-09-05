package com.hospital.controller;

import com.hospital.exception.HospitalException;
import com.hospital.model.User;
import com.hospital.service.PatientService;
import com.hospital.util.TablePrinter;
import java.util.Scanner;

public class PatientMenu {

    private static final PatientService patientService = new PatientService();

    public static void show(Scanner scanner, User user) {
        int patientId = user.takeLinkedId();

        if (patientService.viewPersonalDetails(patientId) == null) {
            System.out.println("No patient found for this account.");
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

            try {
                switch (scanner.nextLine().trim()) {
                case "1" -> patientService.viewPersonalDetailsWithCredentials(patientId, user);
                case "2" -> TablePrinter.printAppointments(patientService.viewAppointments(patientId));
                case "3" -> TablePrinter.printMedicalRecords(patientService.viewMedicalRecords(patientId));
                case "4" -> TablePrinter.printPrescriptions(patientService.viewPrescriptions(patientId));
                case "5" -> patientService.viewFullProfile(patientId, user);
                case "0" -> back = true;
                default  -> System.out.println("Invalid choice.");
                }
            } catch (HospitalException | IllegalArgumentException exception) {
                System.out.println("Operation failed: " + exception.getMessage());
            }
        }
    }
}
