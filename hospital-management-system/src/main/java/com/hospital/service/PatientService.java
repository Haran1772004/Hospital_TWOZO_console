package com.hospital.service;

import com.hospital.exception.AuthorizationException;
import com.hospital.impl.*;
import com.hospital.localfunctions.*;
import com.hospital.model.*;
import com.hospital.util.TablePrinter;
import java.util.ArrayList;
import java.util.List;

public class PatientService {

  private final PatientLF patientLF = new PatientLFImpl();
  private final AppointmentLF appointmentLF = new AppointmentLFImpl();
  private final MedicalRecordLF medicalRecordLF = new MedicalRecordLFImpl();
  private final PrescriptionLF prescriptionLF = new PrescriptionLFImpl();

  public Patient viewPersonalDetails(int patientId) {
    ensurePatientAccess(patientId);
    return patientLF.getPatientById(patientId);
  }

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

  public List<Appointment> viewAppointments(int patientId) {
    ensurePatientAccess(patientId);
    return appointmentLF.getAppointmentsByPatient(patientId);
  }

  public List<MedicalRecord> viewMedicalRecords(int patientId) {
    ensurePatientAccess(patientId);
    return medicalRecordLF.getRecordsByPatient(patientId);
  }

  public List<Prescription> viewPrescriptions(int patientId) {
    ensurePatientAccess(patientId);
    List<Prescription> allPrescriptions = new ArrayList<>();
    List<MedicalRecord> records = medicalRecordLF.getRecordsByPatient(patientId);
    for (MedicalRecord record : records) {
      allPrescriptions.addAll(prescriptionLF.getPrescriptionsByRecord(record.getRecordId()));
    }
    return allPrescriptions;
  }

  public void viewFullProfile(int patientId, User loggedInUser) {

    ensurePatientAccess(patientId);

    System.out.println("========== PATIENT PROFILE ==========");

    Patient patient = viewPersonalDetails(patientId);
    if (patient == null) {
      System.out.println("No such patient found.");
      return;
    }

    System.out.println("\n--- Personal Details ---");
    TablePrinter.printPatient(patient);

    if (loggedInUser != null) {
      System.out.println("\n--- Your Login Username ---");
      System.out.println("  Username : " + loggedInUser.getUsername());
    }

    System.out.println("\n--- Appointments ---");
    TablePrinter.printAppointments(viewAppointments(patientId));

    System.out.println("\n--- Medical Records ---");
    TablePrinter.printMedicalRecords(viewMedicalRecords(patientId));

    System.out.println("\n--- Prescriptions ---");
    TablePrinter.printPrescriptions(viewPrescriptions(patientId));

    System.out.println("\n=======================================");
  }

  public void viewFullProfile(int patientId) {
    viewFullProfile(patientId, null);
  }

  private void ensurePatientAccess(int patientId) {
    User currentUser = AuthService.getCurrentUser();
    if (currentUser != null
        && "PATIENT".equals(currentUser.getRole())
        && currentUser.getLinkedId() != patientId) {
      throw new AuthorizationException("Patients may access only their own records");
    }
  }
}
