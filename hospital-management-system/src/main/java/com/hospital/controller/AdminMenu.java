package com.hospital.controller;

import com.hospital.localfunctions.*;
import com.hospital.impl.*;
import com.hospital.model.Department;
import com.hospital.model.Doctor;
import com.hospital.model.AccountStatus;
import com.hospital.model.User;
import com.hospital.localfunctions.UserLF;
import com.hospital.util.ValidationUtil;
import com.hospital.service.AdminService;
import com.hospital.util.TablePrinter;

import java.util.Scanner;
import java.util.List;

public class AdminMenu {

    private static final DepartmentLF departmentLF = new DepartmentLFImpl();
    private static final DoctorLF doctorLF = new DoctorLFImpl();
    private static final UserLF userLF = new UserLFImpl();
    private static final AdminService adminService = new AdminService();

    public static void show(Scanner scanner) {

        boolean back = false;

        while (!back) {

            System.out.println("\n--- ADMIN MENU ---");
            System.out.println("1.  Add Department");
            System.out.println("2.  Update Department");
            System.out.println("3.  Deactivate Department");
            System.out.println("4.  Activate Department");
            System.out.println("5.  View All Departments");
            System.out.println("6.  Review Pending Doctor Registrations");
            System.out.println("7.  Update Doctor");
            System.out.println("8.  Deactivate Doctor");
            System.out.println("9.  Activate Doctor");
            System.out.println("10. View All Doctors");
            System.out.println("11. View Hospital Records (all)");
            System.out.println("0.  Back to Main Menu");
            System.out.print("Choose option: ");

            String choice = scanner.nextLine().trim();

            try { switch (choice) {
                case "1" -> addDepartment(scanner);
                case "2" -> updateDepartment(scanner);
                case "3" -> deactivateDepartment(scanner);
                case "4" -> activateDepartment(scanner);
                case "5" -> TablePrinter.printDepartments(departmentLF.getAllDepartments());
                case "6" -> reviewPendingDoctors(scanner);
                case "7" -> updateDoctor(scanner);
                case "8" -> deactivateDoctor(scanner);
                case "9" -> activateDoctor(scanner);
                case "10" -> TablePrinter.printDoctors(doctorLF.getAllDoctors());
                case "11" -> adminService.viewHospitalRecords();
                case "0" -> back = true;
                default -> System.out.println("Invalid choice.");
            } } catch (IllegalArgumentException | SecurityException exception) {
                System.out.println("Operation failed: " + exception.getMessage());
            }
        }
    }

    private static void addDepartment(Scanner scanner) {
        String name = "";
        while (true) {
            System.out.print("Name: ");
            name = scanner.nextLine().trim();

            if (!ValidationUtil.isValidDeptName(name)) {
                System.out.println("Name must be between " + ValidationUtil.DEPT_NAME_MIN
                        + " and " + ValidationUtil.DEPT_NAME_MAX + " characters. Try again.");
                continue;
            }

            boolean duplicate = false;
            for (Department d : departmentLF.getAllDepartments()) {
                if (d.getName().equalsIgnoreCase(name)) {
                    duplicate = true;
                    break;
                }
            }
            if (duplicate) {
                System.out.println("A department with this name already exists. Try again.");
                continue;
            }

            break;
        }

        String description = "";
        while (true) {
            System.out.print("Description: ");
            description = scanner.nextLine().trim();

            if (!ValidationUtil.isValidDescription(description)) {
                System.out.println("Description must be between " + ValidationUtil.DESC_MIN
                        + " and " + ValidationUtil.DESC_MAX + " characters. Try again.");
                continue;
            }

            break;
        }

        Department department = new Department(0, name, description, AccountStatus.ACTIVE);
        departmentLF.addDepartment(department);
        // System.out.println("Department added successfully.");
    }

    private static void updateDepartment(Scanner scanner) {
        int id = InputHelper.readInt(scanner, "Department ID to update: ");

        Department existing = departmentLF.getDepartmentById(id);
        if (existing == null) {
            System.out.println("No department found with ID: " + id);
            return;
        }

        System.out.print("New name (leave blank to keep '" + existing.getName() + "'): ");
        String name = scanner.nextLine().trim();
        if (!name.isEmpty()) {
            if (!ValidationUtil.isValidDeptName(name)) {
                System.out.println("Name must be between " + ValidationUtil.DEPT_NAME_MIN
                        + " and " + ValidationUtil.DEPT_NAME_MAX + " characters. Update cancelled.");
                return;
            }

            for (Department d : departmentLF.getAllDepartments()) {
                if (d.getDepartmentId() != existing.getDepartmentId()
                        && d.getName().equalsIgnoreCase(name)) {
                    System.out.println("A department with this name already exists. Update cancelled.");
                    return;
                }
            }

            existing.setName(name);
        }

        System.out.print("New description (leave blank to keep current): ");
        String description = scanner.nextLine().trim();
        if (!description.isEmpty()) {
            if (!ValidationUtil.isValidDescription(description)) {
                System.out.println("Description must be between " + ValidationUtil.DESC_MIN
                        + " and " + ValidationUtil.DESC_MAX + " characters. Update cancelled.");
                return;
            }
            existing.setDescription(description);
        }

        departmentLF.updateDepartment(existing);
        // System.out.println("Department updated successfully.");
    }

    private static void deactivateDepartment(Scanner scanner) {
        int id = InputHelper.readInt(scanner, "Department ID to deactivate: ");
        departmentLF.deactivateDepartment(id);
    }

    private static void activateDepartment(Scanner scanner) {
        int id = InputHelper.readInt(scanner, "Department ID to activate: ");
        departmentLF.activateDepartment(id);
    }

    private static void reviewPendingDoctors(Scanner scanner) {
        List<User> pending = userLF.getPendingUsersByRole("DOCTOR");
        if (pending.isEmpty()) { System.out.println("No pending doctor registrations."); return; }
        for (User user : pending) {
            Doctor doctor = doctorLF.getDoctorById(user.getLinkedId());
            System.out.println("Username: " + user.getUsername() + ", Doctor: " + (doctor == null ? "N/A" : doctor.getName()));
            String action = InputHelper.readText(scanner, "Approve (A) or reject (R): ");
            if ("A".equalsIgnoreCase(action)) { userLF.updateStatus(user.getUsername(), AccountStatus.ACTIVE); if (doctor != null) doctor.setStatus(AccountStatus.ACTIVE); System.out.println("Doctor approved."); }
            else if ("R".equalsIgnoreCase(action)) { userLF.updateStatus(user.getUsername(), AccountStatus.REJECTED); if (doctor != null) doctor.setStatus(AccountStatus.INACTIVE); System.out.println("Doctor rejected."); }
            else System.out.println("Invalid action; left pending.");
        }
    }

    private static void updateDoctor(Scanner scanner) {
        int id = InputHelper.readInt(scanner, "Doctor ID to update: ");

        Doctor existing = doctorLF.getDoctorById(id);
        if (existing == null) {
            System.out.println("No doctor found with ID: " + id);
            return;
        }

        System.out.print("New phone (leave blank to keep current): ");
        String phone = scanner.nextLine().trim();
        if (!phone.isEmpty()) {
            if (!ValidationUtil.isValidPhone(phone)) {
                System.out.println("Invalid phone number. Update cancelled.");
                return;
            }
            if (doctorLF.getAllDoctors().stream().anyMatch(d -> d.getDoctorId() != existing.getDoctorId()
                    && d.getPhone().replaceAll("[^0-9]", "").equals(phone.replaceAll("[^0-9]", "")))) {
                System.out.println("A doctor with this phone already exists. Update cancelled.");
                return;
            }
            existing.setPhone(phone);
        }

        System.out.print("New email (leave blank to keep current): ");
        String email = scanner.nextLine().trim();
        if (!email.isEmpty()) {
            if (!ValidationUtil.isValidEmail(email)) {
                System.out.println("Invalid email format. Update cancelled.");
                return;
            }

            for (Doctor d : doctorLF.getAllDoctors()) {
                if (d.getDoctorId() != existing.getDoctorId()
                        && d.getEmail().trim().equalsIgnoreCase(email.trim())) {
                    System.out.println("A doctor with this email already exists. Update cancelled.");
                    return;
                }
            }

            existing.setEmail(email);
        }

        doctorLF.updateDoctor(existing);
        System.out.println("Doctor updated successfully.");
    }

    private static void deactivateDoctor(Scanner scanner) {
        int id = InputHelper.readInt(scanner, "Doctor ID to deactivate: ");
        doctorLF.deactivateDoctor(id);
    }

    private static void activateDoctor(Scanner scanner) {
        int id = InputHelper.readInt(scanner, "Doctor ID to activate: ");
        doctorLF.activateDoctor(id);
    }
}
