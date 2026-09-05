package com.hospital.impl;

import com.hospital.exception.DuplicateResourceException;
import com.hospital.localfunctions.DoctorLF;
import com.hospital.model.AccountStatus;
import com.hospital.model.Doctor;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class DoctorLFImpl implements DoctorLF {

    private static final List<Doctor> doctors = new ArrayList<>();
    private static final AtomicInteger nextId = new AtomicInteger(1);

    @Override
    public void addDoctor(Doctor doctor) {
        if (doctor == null) {
            throw new IllegalArgumentException("Doctor is required");
        }

        if (doctor.takeDepartment() == null) {
            throw new DuplicateResourceException(
                    "Doctor must belong to a department"
            );
        }

        ensureUnique(doctor);

        if (doctor.takeDoctorId() == 0) {
            doctor.setDoctorId(nextId.getAndIncrement());
        }

        doctors.add(doctor);

        System.out.println(
                "Doctor added successfully (ID: " + doctor.takeDoctorId() + ")"
        );
    }

    @Override
    public void updateDoctor(Doctor doctor) {
        if (doctor == null) {
            throw new IllegalArgumentException("Doctor is required");
        }

        if (takeDoctorById(doctor.takeDoctorId()) == null) {
            System.out.println(
                    "Doctor not found with ID: " + doctor.takeDoctorId()
            );
        } else {
            ensureUnique(doctor);
            System.out.println("Doctor updated successfully");
        }
    }

    @Override
    public void deactivateDoctor(int id) {
        Doctor doctor = takeDoctorById(id);

        if (doctor == null) {
            System.out.println("Doctor not found with ID: " + id);
        } else {
            doctor.setStatus(AccountStatus.INACTIVE);
            System.out.println("Doctor deactivated successfully");
        }
    }

    @Override
    public void activateDoctor(int id) {
        Doctor doctor = takeDoctorById(id);

        if (doctor == null) {
            System.out.println("Doctor not found with ID: " + id);
        } else {
            doctor.setStatus(AccountStatus.ACTIVE);
            System.out.println("Doctor activated successfully");
        }
    }

    @Override
    public Doctor takeDoctorById(int id) {
        return doctors.stream()
                .filter(doctor -> doctor.takeDoctorId() == id)
                .findFirst()
                .orElse(null);
    }

    @Override
    public List<Doctor> takeAllDoctors() {
        return new ArrayList<>(doctors);
    }

    private void ensureUnique(Doctor candidate) {
        String email = normalizeEmail(candidate.takeEmail());
        String phone = normalizePhone(candidate.takePhone());

        boolean duplicate = doctors.stream()
                .filter(existing ->
                        existing.takeDoctorId() != candidate.takeDoctorId()
                )
                .anyMatch(existing ->
                        normalizeEmail(existing.takeEmail()).equals(email)
                                || normalizePhone(existing.takePhone()).equals(phone)
                );

        if (duplicate) {
                throw new DuplicateResourceException(
                    "Doctor email or phone already exists"
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
