package com.hospital.controller;

import java.util.Scanner;

public class MainMenu {

    private static final Scanner scanner = new Scanner(System.in);

    public static void start() {

        boolean running = true;

        while (running) {

            System.out.println("\n===== HOSPITAL MANAGEMENT SYSTEM =====");
            System.out.println("1. Admin");
            System.out.println("2. Receptionist");
            System.out.println("3. Doctor");
            System.out.println("4. Patient");
            System.out.println("5. Billing Staff");
            System.out.println("0. Exit");
            System.out.print("Choose role: ");

            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1" -> AdminMenu.show(scanner);
                case "2" -> ReceptionistMenu.show(scanner);
                case "3" -> DoctorMenu.show(scanner);
                case "4" -> PatientMenu.show(scanner);
                case "5" -> BillingMenu.show(scanner);
                case "0" -> {
                    running = false;
                    System.out.println("Exiting. Goodbye!");
                }
                default -> System.out.println("Invalid choice, try again.");
            }
        }

        scanner.close();
    }
}