package com.hospital;

import com.hospital.controller.MainMenu;
import com.hospital.exception.AuthenticationException;
import com.hospital.model.User;
import com.hospital.service.AuthService;

import java.util.Scanner;

public class App {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        boolean running = true;

        while (running) {
            System.out.println("\n===== HOSPITAL MANAGEMENT SYSTEM LOGIN =====");
            System.out.print("Username (or 'exit' to quit): ");
            String username = scanner.nextLine().trim();

            if (username.equalsIgnoreCase("exit")) {
                running = false;
                continue;
            }

            System.out.print("Password: ");
            String password = scanner.nextLine();

            try {
                User user = AuthService.login(username, password);
                MainMenu.start(scanner, user);
            } catch (AuthenticationException exception) {
                System.out.println(exception.getMessage());
            }
        }

        scanner.close();
        System.out.println("Exiting. Goodbye!");
    }
}