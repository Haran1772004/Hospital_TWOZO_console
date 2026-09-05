package com.hospital.impl;

import com.hospital.exception.DuplicateResourceException;
import com.hospital.localfunctions.PatientLF;
import com.hospital.model.AccountStatus;
import com.hospital.model.Patient;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class PatientLFImpl implements PatientLF {

    private static final List<Patient> patients = new ArrayList<>();
    private static final AtomicInteger nextId = new AtomicInteger(1);

    @Override
    public void addPatient(Patient patient) {
        if (patient == null) {
            throw new IllegalArgumentException("Patient is required");
        }

        ensureUnique(patient);

        if (patient.takePatientId() == 0) {
            patient.setPatientId(nextId.getAndIncrement());
        }

        patients.add(patient);

        System.out.println(
                "Patient added successfully (ID: " + patient.takePatientId() + ")"
        );
    }

    @Override
    public void updatePatient(Patient patient) {
        if (patient == null) {
            throw new IllegalArgumentException("Patient is required");
        }

        if (takePatientById(patient.takePatientId()) == null) {
            System.out.println(
                    "Patient not found with ID: " + patient.takePatientId()
            );
        } else {
            ensureUnique(patient);
            System.out.println("Patient updated successfully");
        }
    }

    @Override
    public void removePatient(int id) {
        if (!patients.removeIf(patient -> patient.takePatientId() == id)) {
            System.out.println("Patient not found with ID: " + id);
        }
    }

    @Override
    public void deactivatePatient(int id) {
        Patient patient = takePatientById(id);

        if (patient == null) {
            System.out.println("Patient not found with ID: " + id);
        } else {
            patient.setStatus(AccountStatus.INACTIVE);
            System.out.println("Patient deactivated successfully");
        }
    }

    @Override
    public void activatePatient(int id) {
        Patient patient = takePatientById(id);

        if (patient == null) {
            System.out.println("Patient not found with ID: " + id);
        } else {
            patient.setStatus(AccountStatus.ACTIVE);
            System.out.println("Patient activated successfully");
        }
    }

    @Override
    public Patient takePatientById(int id) {
        return patients.stream()
                .filter(patient -> patient.takePatientId() == id)
                .findFirst()
                .orElse(null);
    }

    @Override
    public List<Patient> takeAllPatients() {
        return new ArrayList<>(patients);
    }

    private void ensureUnique(Patient candidate) {
        String email = normalizeEmail(candidate.takeEmail());
        String phone = normalizePhone(candidate.takePhone());

        boolean duplicate = patients.stream()
                .filter(existing ->
                        existing.takePatientId() != candidate.takePatientId()
                )
                .anyMatch(existing ->
                        normalizeEmail(existing.takeEmail()).equals(email)
                                || normalizePhone(existing.takePhone()).equals(phone)
                );

        if (duplicate) {
            throw new DuplicateResourceException(
                    "Patient email or phone already exists"
            );
        }
    }

    private String normalizeEmail(String value) {
        return value == null
                ? ""
                : value.trim().toLowerCase();
    }

    private String normalizePhone(String value) {
        return value == null
                ? ""
                : value.replaceAll("[^0-9]", "");
    }
}
