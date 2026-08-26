package com.hospital.controller;

import com.hospital.dao.*;
import com.hospital.impl.*;
import com.hospital.model.Department;
import com.hospital.model.Doctor;
import com.hospital.service.AdminService;
import com.hospital.util.TablePrinter;
import com.hospital.util.UserStore;
import com.hospital.model.User;

import java.util.Scanner;

public class AdminMenu {

    private static final DepartmentDAO departmentDAO = new DepartmentDAOImpl();
    private static final DoctorDAO doctorDAO = new DoctorDAOImpl();
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
            System.out.println("6.  Add Doctor");
            System.out.println("7.  Update Doctor");
            System.out.println("8.  Deactivate Doctor");
            System.out.println("9.  Activate Doctor");
            System.out.println("10. View All Doctors");
            System.out.println("11. View Hospital Records (all)");
            System.out.println("0.  Back to Main Menu");
            System.out.print("Choose option: ");

            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1"  -> addDepartment(scanner);
                case "2"  -> updateDepartment(scanner);
                case "3"  -> deactivateDepartment(scanner);
                case "4"  -> activateDepartment(scanner);
                case "5"  -> TablePrinter.printDepartments(departmentDAO.getAllDepartments());
                case "6"  -> addDoctor(scanner);
                case "7"  -> updateDoctor(scanner);
                case "8"  -> deactivateDoctor(scanner);
                case "9"  -> activateDoctor(scanner);
                case "10" -> TablePrinter.printDoctors(doctorDAO.getAllDoctors());
                case "11" -> adminService.viewHospitalRecords();
                case "0"  -> back = true;
                default   -> System.out.println("Invalid choice.");
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

    private static void activateDepartment(Scanner scanner) {
        System.out.print("Department ID to activate: ");
        int id = Integer.parseInt(scanner.nextLine().trim());
        departmentDAO.activateDepartment(id);
    }

    private static void addDoctor(Scanner scanner) {

        TablePrinter.printDepartments(departmentDAO.getAllDepartments());
        System.out.print("Department ID for this doctor: ");
        int deptId = Integer.parseInt(scanner.nextLine().trim());

        Department department = departmentDAO.getDepartmentById(deptId);
        if (department == null) {
            System.out.println("Invalid department ID.");
            return;
        }

        // Bug fix: reject assignment to an inactive department
        if ("INACTIVE".equals(department.getStatus())) {
            System.out.println("Cannot assign doctor to an inactive department. Please choose an active department.");
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
        String username = "doctor" + doctor.getDoctorId();
        String password = "doctor" + doctor.getDoctorId() + "123";
        UserStore.addUser(new User(username, password, "DOCTOR", doctor.getDoctorId()));
        System.out.println("Doctor login created: " + username + " / " + password);
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

    private static void activateDoctor(Scanner scanner) {
        System.out.print("Doctor ID to activate: ");
        int id = Integer.parseInt(scanner.nextLine().trim());
        doctorDAO.activateDoctor(id);
    }
}
