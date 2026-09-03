package com.hospital.controller;

import com.hospital.localfunctions.DepartmentLF;
import com.hospital.impl.DepartmentLFImpl;
import com.hospital.model.AccountStatus;
import com.hospital.model.Department;
import com.hospital.model.Gender;
import com.hospital.service.RegistrationService;
import com.hospital.util.TablePrinter;
import com.hospital.util.ValidationUtil;

import java.util.Scanner;

public final class AuthMenu {

    private static final RegistrationService registrationService =
            new RegistrationService();
    private static final DepartmentLF departmentLF = new DepartmentLFImpl();

    private AuthMenu() {
    }

    public static void start(Scanner scanner) {
        boolean running = true;

        while (running) {

            System.out.println("\n===== HOSPITAL MANAGEMENT SYSTEM =====");
            System.out.println("1. Login");
            System.out.println("2. Register as Patient");
            System.out.println("3. Register as Doctor");
            System.out.println("4. Exit");
            System.out.print("Choose option: ");

            switch (scanner.nextLine().trim()) {
                case "1" -> login(scanner);
                case "2" -> registerPatient(scanner);
                case "3" -> registerDoctor(scanner);
                case "4" -> running = false;
                default -> System.out.println("Invalid choice.");
            }
        }
    }

    private static void login(Scanner scanner) {
        System.out.print("Username: ");
        String username = scanner.nextLine().trim();

        System.out.print("Password: ");
        String password = scanner.nextLine();

        try {
            MainMenu.start(
                    scanner,
                    com.hospital.service.AuthService.login(username, password)
            );
        } catch (RuntimeException exception) {
            System.out.println(exception.getMessage());
        }
    }

    private static void registerPatient(Scanner scanner) {
        try {
            String username = readUsername(scanner);

            while (!registrationService.isUsernameAvailable(username)) {
                System.out.println("Username already exists. Try again.");
                username = readUsername(scanner);
            }

            String password = readPassword(scanner);
            String name = readName(scanner, "Name: ");
            String dob = readDate(scanner, "DOB (yyyy-MM-dd): ");
            String gender = readGender(scanner);
            String phone = readPhone(scanner);

            while (!registrationService.isPatientPhoneAvailable(phone)) {
                System.out.println("Phone already exists. Try again.");
                phone = readPhone(scanner);
            }

            String email = readEmail(scanner);

            while (!registrationService.isPatientEmailAvailable(email)) {
                System.out.println("Email already exists. Try again.");
                email = readEmail(scanner);
            }

            String address = readAddress(scanner);

            registrationService.registerPatient(
                    username,
                    password,
                    name,
                    dob,
                    gender,
                    phone,
                    email,
                    address
            );

            System.out.println(
                    "Patient registration submitted. Await admin approval before logging in."
            );

        } catch (IllegalArgumentException exception) {
            System.out.println("Registration failed: " + exception.getMessage());
        }
    }

    private static void registerDoctor(Scanner scanner) {
        try {
            String username = readUsername(scanner);

            while (!registrationService.isUsernameAvailable(username)) {
                System.out.println("Username already exists. Try again.");
                username = readUsername(scanner);
            }

            String password = readPassword(scanner);
            String name = readName(scanner, "Doctor name: ");
            String specialization = readSpecialization(scanner);
            String phone = readPhone(scanner);

            while (!registrationService.isDoctorPhoneAvailable(phone)) {
                System.out.println("Phone already exists. Try again.");
                phone = readPhone(scanner);
            }

            String email = readEmail(scanner);

            while (!registrationService.isDoctorEmailAvailable(email)) {
                System.out.println("Email already exists. Try again.");
                email = readEmail(scanner);
            }

            TablePrinter.printDepartments(
                    departmentLF.getAllDepartments()
                            .stream()
                            .filter(department ->
                                    AccountStatus.ACTIVE == department.getStatus())
                            .toList()
            );

            int departmentId =
                    InputHelper.readInt(scanner, "Active department ID: ");

            Department department =
                    departmentLF.getDepartmentById(departmentId);

            registrationService.registerDoctor(
                    username,
                    password,
                    name,
                    specialization,
                    phone,
                    email,
                    department
            );

            System.out.println(
                    "Doctor registration submitted. Await admin approval before logging in."
            );

        } catch (IllegalArgumentException exception) {
            System.out.println("Registration failed: " + exception.getMessage());
        }
    }

    private static String readUsername(Scanner scanner) {
        while (true) {
            String value = readText(
                    scanner,
                    "Username (3-20 letters, numbers, or _): "
            );

            if (ValidationUtil.isValidUsername(value)) {
                return value;
            }

            System.out.println(
                    "Invalid username: use 3-20 letters, numbers, or underscores."
            );
        }
    }

    private static String readName(Scanner scanner, String prompt) {
        while (true) {
            String value = readText(scanner, prompt);

            if (ValidationUtil.isValidName(value)) {
                return value;
            }

            System.out.println(
                    "Invalid name: enter 2-60 non-blank characters."
            );
        }
    }

    private static String readDate(Scanner scanner, String prompt) {
        while (true) {
            String value = readText(scanner, prompt);

            if (ValidationUtil.isValidDate(value)) {
                return value;
            }

            System.out.println(
                    "Invalid date: use yyyy-MM-dd and enter a real date."
            );
        }
    }

    private static String readGender(Scanner scanner) {
        while (true) {
            String value = readText(
                    scanner,
                    "Gender (MALE/FEMALE/OTHER): "
            );

            try {
                return Gender.valueOf(value.toUpperCase()).name();
            } catch (RuntimeException exception) {
                System.out.println(
                        "Invalid gender: choose MALE, FEMALE, or OTHER."
                );
            }
        }
    }

    private static String readPhone(Scanner scanner) {
        while (true) {
            String value = readText(scanner, "Phone: ");

            if (ValidationUtil.isValidPhone(value)) {
                return value;
            }

            System.out.println(
                    "Invalid phone: use 7-15 permitted characters."
            );
        }
    }

    private static String readEmail(Scanner scanner) {
        while (true) {
            String value = readText(scanner, "Email: ");

            if (ValidationUtil.isValidEmail(value)) {
                return value;
            }

            System.out.println("Invalid email format.");
        }
    }

    private static String readAddress(Scanner scanner) {
        while (true) {
            String value = readText(scanner, "Address: ");

            if (ValidationUtil.isValidAddress(value)) {
                return value;
            }

            System.out.println(
                    "Invalid address: enter 5-100 non-blank characters."
            );
        }
    }

    private static String readSpecialization(Scanner scanner) {
        while (true) {
            String value = readText(scanner, "Specialization: ");

            if (ValidationUtil.isValidSpecialization(value)) {
                return value;
            }

            System.out.println(
                    "Invalid specialization: enter at least 2 non-blank characters."
            );
        }
    }

    private static String readPassword(Scanner scanner) {
        while (true) {
            String value = readText(scanner, "Password: ");

            if (ValidationUtil.isStrongPassword(value)) {
                return value;
            }

            System.out.println(
                    "Password too weak: use at least 6 characters with letters, numbers, and a symbol."
            );
        }
    }

    private static String readText(Scanner scanner, String prompt) {
        return InputHelper.readText(scanner, prompt);
    }
}

