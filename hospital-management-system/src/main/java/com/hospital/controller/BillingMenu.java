package com.hospital.controller;

import com.hospital.dao.BillDAO;
import com.hospital.dao.PatientDAO;
import com.hospital.impl.BillDAOImpl;
import com.hospital.impl.PatientDAOImpl;
import com.hospital.model.Bill;
import com.hospital.model.Patient;
import com.hospital.model.Payment;
import com.hospital.service.BillingService;

import java.math.BigDecimal;
import java.util.Scanner;

public class BillingMenu {

    private static final BillDAO billDAO = new BillDAOImpl();
    private static final PatientDAO patientDAO = new PatientDAOImpl();
    private static final BillingService billingService = new BillingService();

    public static void show(Scanner scanner) {

        boolean back = false;

        while (!back) {

            System.out.println("\n--- BILLING MENU ---");
            System.out.println("1. Generate Bill");
            System.out.println("2. Record Payment");
            System.out.println("3. View Payment History (single bill)");
            System.out.println("4. View All Bills");
            System.out.println("0. Back to Main Menu");
            System.out.print("Choose option: ");

            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1" -> generateBill(scanner);
                case "2" -> recordPayment(scanner);
                case "3" -> viewPaymentHistory(scanner);
                case "4" -> billDAO.getAllBills().forEach(System.out::println);
                case "0" -> back = true;
                default -> System.out.println("Invalid choice.");
            }
        }
    }

    private static void generateBill(Scanner scanner) {

        System.out.println("\nAvailable patients:");
        patientDAO.getAllPatients().forEach(System.out::println);
        int patientId = InputHelper.readInt(scanner, "Patient ID: ");
        Patient patient = patientDAO.getPatientById(patientId);

        if (patient == null) {
            System.out.println("Invalid patient ID.");
            return;
        }

        BigDecimal consultation = InputHelper.readBigDecimal(scanner, "Consultation charge: ");
        BigDecimal medicine = InputHelper.readBigDecimal(scanner, "Medicine charge: ");
        BigDecimal other = InputHelper.readBigDecimal(scanner, "Other charge: ");
        String billDate = InputHelper.readText(scanner, "Bill date (yyyy-MM-dd): ");

        Bill bill = new Bill(0, patient, consultation, medicine, other, BigDecimal.ZERO, billDate, "UNPAID");
        billDAO.generateBill(bill);
    }

    private static void recordPayment(Scanner scanner) {

        int billId = InputHelper.readInt(scanner, "Bill ID: ");
        Bill bill = billDAO.getBillById(billId);

        if (bill == null) {
            System.out.println("No bill found with that ID.");
            return;
        }

        System.out.println(bill);

        BigDecimal amount = InputHelper.readBigDecimal(scanner, "Amount paid: ");
        String date = InputHelper.readText(scanner, "Payment date (yyyy-MM-dd): ");
        String method = InputHelper.readText(scanner, "Payment method (CASH/CARD/UPI): ");

        billingService.makePayment(new Payment(0, billId, amount, date, method));
    }

    private static void viewPaymentHistory(Scanner scanner) {
        int billId = InputHelper.readInt(scanner, "Bill ID: ");
        billingService.viewPaymentHistory(billId);
    }
}