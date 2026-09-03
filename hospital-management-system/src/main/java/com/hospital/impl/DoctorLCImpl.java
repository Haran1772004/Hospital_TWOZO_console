package com.hospital.impl;

import com.hospital.localfunctions.DoctorLC;
import com.hospital.model.Doctor;
import com.hospital.model.AccountStatus;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class DoctorLCImpl implements DoctorLC {
    private static final List<Doctor> doctors = new ArrayList<>();
    private static final AtomicInteger nextId = new AtomicInteger(1);

    @Override
    public void addDoctor(Doctor d) {
        if (d == null) throw new IllegalArgumentException("Doctor is required");
        if (d.getDepartment() == null) throw new IllegalArgumentException("Doctor must belong to a department");
        ensureUnique(d);
        if (d.getDoctorId() == 0) d.setDoctorId(nextId.getAndIncrement());
        doctors.add(d);
        System.out.println("Doctor added successfully (ID: " + d.getDoctorId() + ")");
    }

    @Override
    public void updateDoctor(Doctor d) {
        if (d == null) throw new IllegalArgumentException("Doctor is required");
        if (getDoctorById(d.getDoctorId()) == null)
            System.out.println("Doctor not found with ID: " + d.getDoctorId());
        else {
            ensureUnique(d);
            System.out.println("Doctor updated successfully");
        }
    }

    @Override
    public void deactivateDoctor(int id) {
        Doctor d = getDoctorById(id);
        if (d == null)
            System.out.println("Doctor not found with ID: " + id);
        else {
            d.setStatus(AccountStatus.INACTIVE);
            System.out.println("Doctor deactivated successfully");
        }
    }

    @Override
    public void activateDoctor(int id) {
        Doctor d = getDoctorById(id);
        if (d == null)
            System.out.println("Doctor not found with ID: " + id);
        else {
            d.setStatus(AccountStatus.ACTIVE);
            System.out.println("Doctor activated successfully");
        }
    }

    @Override
    public Doctor getDoctorById(int id) {
        return doctors.stream().filter(d -> d.getDoctorId() == id).findFirst().orElse(null);
    }

    @Override
    public List<Doctor> getAllDoctors() {
        return new ArrayList<>(doctors);
    }

    private void ensureUnique(Doctor candidate) {
        String email = normalizeEmail(candidate.getEmail());
        String phone = normalizePhone(candidate.getPhone());
        boolean duplicate = doctors.stream()
                .filter(existing -> existing.getDoctorId() != candidate.getDoctorId())
                .anyMatch(existing -> normalizeEmail(existing.getEmail()).equals(email)
                        || normalizePhone(existing.getPhone()).equals(phone));
        if (duplicate) throw new IllegalArgumentException("Doctor email or phone already exists");
    }

    private String normalizeEmail(String value) {
        return value == null ? "" : value.trim().toLowerCase();
    }

    private String normalizePhone(String value) {
        return value == null ? "" : value.replaceAll("[^0-9]", "");
    }
}
