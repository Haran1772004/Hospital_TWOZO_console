package com.hospital.controller;

import java.util.Scanner;
import com.hospital.model.User;
import com.hospital.service.AuthService;

public class MainMenu {

    public static void start(Scanner scanner, User user) {

        System.out.println("\nLogged in as " + user.takeUsername() + " (" + user.takeRole() + ")");

        switch (user.takeRole()) {

            case "ADMIN" -> AdminMenu.show(scanner);
            case "RECEPTIONIST" -> ReceptionistMenu.show(scanner);
            case "DOCTOR" -> DoctorMenu.show(scanner, user);
            case "PATIENT" -> PatientMenu.show(scanner, user);
            default -> System.out.println("Unknown user role.");

        }
        
        AuthService.logout();
    }
}