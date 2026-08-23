package com.hospital.controller;

import com.hospital.dao.*;
import com.hospital.impl.*;
import com.hospital.model.Department;
import com.hospital.model.Doctor;
import com.hospital.service.AdminService;

import java.util.Scanner;

public class AdminMenu {

    private static final DepartmentDAO departmentDAO = new DepartmentDAOImpl();
    private static final DoctorDAO doctorDAO = new DoctorDAOImpl();
    private static final AdminService adminService = new AdminService();

    public static void show(Scanner scanner) {

        boolean back = false;

        while (!back) {

            System.out.println("\n--- ADMIN MENU ---");
            System.out.println("1. Add Department");
            System.out.println("2. Update Department");
            System.out.println("3. Deactivate Department");
            System.out.println("4. View All Departments");
            System.out.println("5. Add Doctor");
            System.out.println("6. Update Doctor");
            System.out.println("7. Deactivate Doctor");
            System.out.println("8. View All Doctors");
            System.out.println("9. View Hospital Records (all)");
            System.out.println("0. Back to Main Menu");
            System.out.print("Choose option: ");

            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1" -> addDepartment(scanner);
                case "2" -> updateDepartment(scanner);
                case "3" -> deactivateDepartment(scanner);
                case "4" -> departmentDAO.getAllDepartments().forEach(System.out::println);
                case "5" -> addDoctor(scanner);
                case "6" -> updateDoctor(scanner);
                case "7" -> deactivateDoctor(scanner);
                case "8" -> doctorDAO.getAllDoctors().forEach(System.out::println);
                case "9" -> adminService.viewHospitalRecords();
                case "0" -> back = true;
                default -> System.out.println("Invalid choice.");
            }
        }
    }

    private static void addDepartment(Scanner scanner) {
        System.out.print("Name: ");
        String name = scanner.nextLine();
        System.out.print("Description: ");
        String description = scanner.nextLine();

        Department department = new Department(0, name, description, "ACTIVE");
        departmentDAO.addDepartment(department);
    }

    private static void updateDepartment(Scanner scanner) {
        System.out.print("Department ID to update: ");
        int id = Integer.parseInt(scanner.nextLine().trim());

        Department existing = departmentDAO.getDepartmentById(id);
        if (existing == null) {
            System.out.println("No department found with ID: " + id);
            return;
        }

        System.out.print("New name (leave blank to keep '" + existing.getName() + "'): ");
        String name = scanner.nextLine();
        if (!name.isBlank()) existing.setName(name);

        System.out.print("New description (leave blank to keep current): ");
        String description = scanner.nextLine();
        if (!description.isBlank()) existing.setDescription(description);

        departmentDAO.updateDepartment(existing);
    }

    private static void deactivateDepartment(Scanner scanner) {
        System.out.print("Department ID to deactivate: ");
        int id = Integer.parseInt(scanner.nextLine().trim());
        departmentDAO.deactivateDepartment(id);
    }

    private static void addDoctor(Scanner scanner) {

        departmentDAO.getAllDepartments().forEach(System.out::println);
        System.out.print("Department ID for this doctor: ");
        int deptId = Integer.parseInt(scanner.nextLine().trim());

        Department department = departmentDAO.getDepartmentById(deptId);
        if (department == null) {
            System.out.println("Invalid department ID.");
            return;
        }

        System.out.print("Doctor name: ");
        String name = scanner.nextLine();
        System.out.print("Specialization: ");
        String specialization = scanner.nextLine();
        System.out.print("Phone: ");
        String phone = scanner.nextLine();
        System.out.print("Email: ");
        String email = scanner.nextLine();

        Doctor doctor = new Doctor(0, name, specialization, phone, email, department, "ACTIVE");
        doctorDAO.addDoctor(doctor);
    }

    private static void updateDoctor(Scanner scanner) {
        System.out.print("Doctor ID to update: ");
        int id = Integer.parseInt(scanner.nextLine().trim());

        Doctor existing = doctorDAO.getDoctorById(id);
        if (existing == null) {
            System.out.println("No doctor found with ID: " + id);
            return;
        }

        System.out.print("New phone (leave blank to keep current): ");
        String phone = scanner.nextLine();
        if (!phone.isBlank()) existing.setPhone(phone);

        System.out.print("New email (leave blank to keep current): ");
        String email = scanner.nextLine();
        if (!email.isBlank()) existing.setEmail(email);

        doctorDAO.updateDoctor(existing);
    }

    private static void deactivateDoctor(Scanner scanner) {
        System.out.print("Doctor ID to deactivate: ");
        int id = Integer.parseInt(scanner.nextLine().trim());
        doctorDAO.deactivateDoctor(id);
    }
}