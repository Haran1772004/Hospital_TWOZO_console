package com.hospital.service;

import com.hospital.localfunctions.*;
import com.hospital.impl.*;
import com.hospital.model.*;
import com.hospital.util.TablePrinter;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class PatientService {

    private final PatientLF     patientLF     = new PatientLFImpl();
    private final AppointmentLF appointmentLF = new AppointmentLFImpl();
    private final MedicalRecordLF medicalRecordLF = new MedicalRecordLFImpl();
    private final PrescriptionLF prescriptionLF = new PrescriptionLFImpl();
    private final BillLF        billLF        = new BillLFImpl();
    private final PaymentLF     paymentLF     = new PaymentLFImpl();

    // 1. VIEW PERSONAL DETAILS
    public Patient viewPersonalDetails(int patientId) {
        ensurePatientAccess(patientId);
        return patientLF.getPatientById(patientId);
    }

    /** Prints the patient's own personal details and username. */
    public void viewPersonalDetailsWithCredentials(int patientId, User loggedInUser) {
        Patient patient = patientLF.getPatientById(patientId);
        if (patient == null) {
            System.out.println("No patient found with ID: " + patientId);
            return;
        }

        System.out.println("\n--- Personal Details ---");
        TablePrinter.printPatient(patient);

        if (loggedInUser != null) {
            System.out.println("\n--- Your Login Username ---");
            System.out.println("  Username : " + loggedInUser.getUsername());
        }
    }

    // 2. VIEW APPOINTMENTS (ordered by date)
    public List<Appointment> viewAppointments(int patientId) {
        ensurePatientAccess(patientId);
        return appointmentLF.getAppointmentsByPatient(patientId);
    }

    // 3. VIEW MEDICAL RECORDS
    public List<MedicalRecord> viewMedicalRecords(int patientId) {
        ensurePatientAccess(patientId);
        return medicalRecordLF.getRecordsByPatient(patientId);
    }

    // 4. VIEW PRESCRIPTIONS (across ALL of the patient's medical records)
    public List<Prescription> viewPrescriptions(int patientId) {
        ensurePatientAccess(patientId);
        List<Prescription> allPrescriptions = new ArrayList<>();
        List<MedicalRecord> records = medicalRecordLF.getRecordsByPatient(patientId);
        for (MedicalRecord record : records) {
            allPrescriptions.addAll(prescriptionLF.getPrescriptionsByRecord(record.getRecordId()));
        }
        return allPrescriptions;
    }

    /**
     * Sums every payment recorded against a bill.
     * Mirrors BillingService.getTotalPaid() — kept here to avoid a cross-service dependency.
     */
    private BigDecimal getTotalPaid(int billId) {
        BigDecimal total = BigDecimal.ZERO;
        for (Payment p : paymentLF.getPaymentsByBill(billId)) {
            total = total.add(p.getAmountPaid());
        }
        return total;
    }

    /** Prints the patient's complete profile without exposing password data. */
    public void viewFullProfile(int patientId, User loggedInUser) {

        ensurePatientAccess(patientId);

        System.out.println("========== PATIENT PROFILE ==========");

        Patient patient = viewPersonalDetails(patientId);
        if (patient == null) {
            System.out.println("No such patient found.");
            return;
        }

        // Personal details
        System.out.println("\n--- Personal Details ---");
        TablePrinter.printPatient(patient);

        if (loggedInUser != null) {
            System.out.println("\n--- Your Login Username ---");
            System.out.println("  Username : " + loggedInUser.getUsername());
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
        List<Bill> bills = billLF.getBillsByPatient(patientId);
        if (bills.isEmpty()) {
            System.out.println("No bills found.");
        } else {
            TablePrinter.printBills(bills);

            // For each bill: show its payments, total paid, and remaining balance
            for (Bill bill : bills) {
                System.out.println("\n  Payments for Bill #" + bill.getBillId()
                        + " (Total: " + bill.getTotalAmount() + ")");
                List<Payment> payments = paymentLF.getPaymentsByBill(bill.getBillId());
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

    private void ensurePatientAccess(int patientId) {
        User currentUser = AuthService.getCurrentUser();
        if (currentUser != null && "PATIENT".equals(currentUser.getRole())
                && currentUser.getLinkedId() != patientId) {
            throw new SecurityException("Patients may access only their own records");
        }
    }
}
