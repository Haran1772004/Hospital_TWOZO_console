package com.hospital.impl;

import com.hospital.localfunctions.PatientLC;
import com.hospital.model.Patient;
import com.hospital.model.AccountStatus;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class PatientLCImpl implements PatientLC {
    private static final List<Patient> patients = new ArrayList<>();
    private static final AtomicInteger nextId = new AtomicInteger(1);

    @Override
    public void addPatient(Patient patient) {
        if (patient == null) throw new IllegalArgumentException("Patient is required");
        ensureUnique(patient);
        if (patient.getPatientId() == 0) patient.setPatientId(nextId.getAndIncrement());
        patients.add(patient);
        System.out.println("Patient added successfully (ID: " + patient.getPatientId() + ")");
    }

    @Override
    public void updatePatient(Patient patient) {
        if (patient == null) throw new IllegalArgumentException("Patient is required");
        if (getPatientById(patient.getPatientId()) == null)
            System.out.println("Patient not found with ID: " + patient.getPatientId());
        else {
            ensureUnique(patient);
            System.out.println("Patient updated successfully");
        }
    }

    @Override
    public void removePatient(int id) {
        if (!patients.removeIf(patient -> patient.getPatientId() == id)) {
            System.out.println("Patient not found with ID: " + id);
        }
    }

    @Override
    public void deactivatePatient(int id) {
        Patient p = getPatientById(id);
        if (p == null)
            System.out.println("Patient not found with ID: " + id);
        else {
            p.setStatus(AccountStatus.INACTIVE);
            System.out.println("Patient deactivated successfully");
        }
    }

    @Override
    public void activatePatient(int id) {
        Patient p = getPatientById(id);
        if (p == null)
            System.out.println("Patient not found with ID: " + id);
        else {
            p.setStatus(AccountStatus.ACTIVE);
            System.out.println("Patient activated successfully");
        }
    }

    @Override
    public Patient getPatientById(int id) {
        return patients.stream().filter(p -> p.getPatientId() == id).findFirst().orElse(null);
    }

    @Override
    public List<Patient> getAllPatients() {
        return new ArrayList<>(patients);
    }

    private void ensureUnique(Patient candidate) {
        String email = normalizeEmail(candidate.getEmail());
        String phone = normalizePhone(candidate.getPhone());
        boolean duplicate = patients.stream()
                .filter(existing -> existing.getPatientId() != candidate.getPatientId())
                .anyMatch(existing -> normalizeEmail(existing.getEmail()).equals(email)
                        || normalizePhone(existing.getPhone()).equals(phone));
        if (duplicate) throw new IllegalArgumentException("Patient email or phone already exists");
    }

    private String normalizeEmail(String value) {
        return value == null ? "" : value.trim().toLowerCase();
    }

    private String normalizePhone(String value) {
        return value == null ? "" : value.replaceAll("[^0-9]", "");
    }
}
