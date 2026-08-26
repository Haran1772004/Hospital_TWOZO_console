package com.hospital.impl;

import com.hospital.dao.PatientDAO;
import com.hospital.model.Patient;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class PatientDAOImpl implements PatientDAO {
    private static final List<Patient> patients = new ArrayList<>();
    private static final AtomicInteger nextId = new AtomicInteger(1);

    @Override
    public void addPatient(Patient patient) {
        if (patient.getPatientId() == 0) patient.setPatientId(nextId.getAndIncrement());
        patients.add(patient);
        System.out.println("Patient added successfully (ID: " + patient.getPatientId() + ")");
    }

    @Override
    public void updatePatient(Patient patient) {
        if (getPatientById(patient.getPatientId()) == null)
            System.out.println("Patient not found with ID: " + patient.getPatientId());
        else
            System.out.println("Patient updated successfully");
    }

    @Override
    public void deactivatePatient(int id) {
        Patient p = getPatientById(id);
        if (p == null)
            System.out.println("Patient not found with ID: " + id);
        else {
            p.setStatus("INACTIVE");
            System.out.println("Patient deactivated successfully");
        }
    }

    @Override
    public void activatePatient(int id) {
        Patient p = getPatientById(id);
        if (p == null)
            System.out.println("Patient not found with ID: " + id);
        else {
            p.setStatus("ACTIVE");
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
}
