package com.hospital.service;

import com.hospital.dao.*;
import com.hospital.impl.*;
import com.hospital.model.*;
import com.hospital.util.TablePrinter;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class PatientService {

    private final PatientDAO     patientDAO     = new PatientDAOImpl();
    private final AppointmentDAO appointmentDAO = new AppointmentDAOImpl();
    private final MedicalRecordDAO medicalRecordDAO = new MedicalRecordDAOImpl();
    private final PrescriptionDAO prescriptionDAO = new PrescriptionDAOImpl();
    private final BillDAO        billDAO        = new BillDAOImpl();
    private final PaymentDAO     paymentDAO     = new PaymentDAOImpl();

    // 1. VIEW PERSONAL DETAILS
    public Patient viewPersonalDetails(int patientId) {
        return patientDAO.getPatientById(patientId);
    }

    /**
     * Prints the patient's own personal details plus their login credentials.
     * Safe to display because the patient is only viewing their own account.
     */
    public void viewPersonalDetailsWithCredentials(int patientId, User loggedInUser) {
        Patient patient = patientDAO.getPatientById(patientId);
        if (patient == null) {
            System.out.println("No patient found with ID: " + patientId);
            return;
        }

        System.out.println("\n--- Personal Details ---");
        TablePrinter.printPatient(patient);

        // Show the patient's own login credentials (patient views only their own — not a security risk)
        if (loggedInUser != null) {
            System.out.println("\n--- Your Login Credentials ---");
            System.out.println("  Username : " + loggedInUser.getUsername());
            System.out.println("  Password : " + loggedInUser.getPassword());
        }
    }

    // 2. VIEW APPOINTMENTS (ordered by date)
    public List<Appointment> viewAppointments(int patientId) {
        return appointmentDAO.getAppointmentsByPatient(patientId);
    }

    // 3. VIEW MEDICAL RECORDS
    public List<MedicalRecord> viewMedicalRecords(int patientId) {
        return medicalRecordDAO.getRecordsByPatient(patientId);
    }

    // 4. VIEW PRESCRIPTIONS (across ALL of the patient's medical records)
    public List<Prescription> viewPrescriptions(int patientId) {
        List<Prescription> allPrescriptions = new ArrayList<>();
        List<MedicalRecord> records = medicalRecordDAO.getRecordsByPatient(patientId);
        for (MedicalRecord record : records) {
            allPrescriptions.addAll(prescriptionDAO.getPrescriptionsByRecord(record.getRecordId()));
        }
        return allPrescriptions;
    }

    /**
     * Sums every payment recorded against a bill.
     * Mirrors BillingService.getTotalPaid() — kept here to avoid a cross-service dependency.
     */
    private BigDecimal getTotalPaid(int billId) {
        BigDecimal total = BigDecimal.ZERO;
        for (Payment p : paymentDAO.getPaymentsByBill(billId)) {
            total = total.add(p.getAmountPaid());
        }
        return total;
    }

    /**
     * Prints the patient's complete profile: personal details + credentials,
     * appointments, medical records, prescriptions, and full billing information.
     */
    public void viewFullProfile(int patientId, User loggedInUser) {

        System.out.println("========== PATIENT PROFILE ==========");

        Patient patient = viewPersonalDetails(patientId);
        if (patient == null) {
            System.out.println("No such patient found.");
            return;
        }

        // Personal details
        System.out.println("\n--- Personal Details ---");
        TablePrinter.printPatient(patient);

        // Own credentials (safe: patient viewing their own data only)
        if (loggedInUser != null) {
            System.out.println("\n--- Your Login Credentials ---");
            System.out.println("  Username : " + loggedInUser.getUsername());
            System.out.println("  Password : " + loggedInUser.getPassword());
        }

        // Appointments
        System.out.println("\n--- Appointments ---");
        TablePrinter.printAppointments(viewAppointments(patientId));

        // Medical records
        System.out.println("\n--- Medical Records ---");
        TablePrinter.printMedicalRecords(viewMedicalRecords(patientId));

        // Prescriptions
        System.out.println("\n--- Prescriptions ---");
        TablePrinter.printPrescriptions(viewPrescriptions(patientId));

        // Billing information
        System.out.println("\n--- Billing Information ---");
        List<Bill> bills = billDAO.getBillsByPatient(patientId);
        if (bills.isEmpty()) {
            System.out.println("No bills found.");
        } else {
            TablePrinter.printBills(bills);

            // For each bill: show its payments, total paid, and remaining balance
            for (Bill bill : bills) {
                System.out.println("\n  Payments for Bill #" + bill.getBillId()
                        + " (Total: " + bill.getTotalAmount() + ")");
                List<Payment> payments = paymentDAO.getPaymentsByBill(bill.getBillId());
                if (payments.isEmpty()) {
                    System.out.println("  No payments recorded yet.");
                } else {
                    TablePrinter.printPayments(payments);
                }
                BigDecimal totalPaid  = getTotalPaid(bill.getBillId());
                BigDecimal remaining  = bill.getTotalAmount().subtract(totalPaid);
                System.out.println("  Total Paid     : " + totalPaid);
                System.out.println("  Remaining Bal  : " + remaining);
            }
        }

        System.out.println("\n=======================================");
    }

    /**
     * Kept for backward compatibility (e.g., AdminService calling without a User object).
     * Prints full profile without credentials section.
     */
    public void viewFullProfile(int patientId) {
        viewFullProfile(patientId, null);
    }
}
