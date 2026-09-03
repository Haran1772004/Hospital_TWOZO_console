package com.hospital.controller;

import com.hospital.localfunctions.BillLC;
import com.hospital.impl.BillLCImpl;
import com.hospital.localfunctions.AppointmentLC;
import com.hospital.impl.AppointmentLCImpl;
import com.hospital.model.Appointment;
import com.hospital.model.Bill;
import com.hospital.model.Payment;
import com.hospital.model.PaymentMethod;
import com.hospital.model.BillStatus;
import com.hospital.service.BillingService;
import com.hospital.util.TablePrinter;
import com.hospital.util.ValidationUtil;

import java.math.BigDecimal;
import java.util.Scanner;

public class BillingMenu {

    private static final BillLC billLC = new BillLCImpl();
    private static final AppointmentLC appointmentLC = new AppointmentLCImpl();
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

            try { switch (choice) {
                case "1" -> generateBill(scanner);
                case "2" -> recordPayment(scanner);
                case "3" -> viewPaymentHistory(scanner);
                case "4" -> TablePrinter.printBills(billLC.getAllBills());
                case "0" -> back = true;
                default -> System.out.println("Invalid choice.");
            } } catch (IllegalArgumentException | SecurityException exception) {
                System.out.println("Operation failed: " + exception.getMessage());
            }
        }
    }

    private static void generateBill(Scanner scanner) {

        System.out.println("\nAvailable appointments:");
        TablePrinter.printAppointments(appointmentLC.getAllAppointments());
        int appointmentId = InputHelper.readInt(scanner, "Appointment ID: ");
        Appointment appointment = appointmentLC.getAllAppointments().stream()
                .filter(candidate -> candidate.getAppointmentId() == appointmentId).findFirst().orElse(null);
        if (appointment == null) {
            System.out.println("Invalid appointment ID.");
            return;
        }

        BigDecimal consultation = InputHelper.readBigDecimal(scanner, "Consultation charge: ");
        BigDecimal medicine = InputHelper.readBigDecimal(scanner, "Medicine charge: ");
        BigDecimal other = InputHelper.readBigDecimal(scanner, "Other charge: ");
        String billDate = InputHelper.readText(scanner, "Bill date (yyyy-MM-dd): ");

        Bill bill = new Bill(0, appointment, consultation, medicine, other, BigDecimal.ZERO, billDate, BillStatus.UNPAID);
        billLC.generateBill(bill);
    }

    private static void recordPayment(Scanner scanner) {

        int billId = InputHelper.readInt(scanner, "Bill ID: ");
        Bill bill = billLC.getBillById(billId);

        if (bill == null) {
            System.out.println("No bill found with that ID.");
            return;
        }

        TablePrinter.printBill(bill);

        BigDecimal amount = InputHelper.readBigDecimal(scanner, "Amount paid: ");
        String date = InputHelper.readText(scanner, "Payment date (yyyy-MM-dd): ");
        PaymentMethod method = readPaymentMethod(scanner);

        billingService.makePayment(new Payment(0, billId, amount, date, method));
    }

    private static PaymentMethod readPaymentMethod(Scanner scanner) {
        while (true) {
            String value = InputHelper.readText(scanner, "Payment method (CASH/CARD/UPI): ");
            if (ValidationUtil.isValidPaymentMethod(value)) {
                return PaymentMethod.valueOf(value.trim().toUpperCase());
            }
            System.out.println("Invalid payment method. Choose CASH, CARD, or UPI.");
        }
    }

    private static void viewPaymentHistory(Scanner scanner) {
        int billId = InputHelper.readInt(scanner, "Bill ID: ");
        billingService.viewPaymentHistory(billId);
    }
}