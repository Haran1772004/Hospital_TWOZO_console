package com.hospital.impl;

import com.hospital.dao.DoctorDAO;
import com.hospital.model.Doctor;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class DoctorDAOImpl implements DoctorDAO {
    private static final List<Doctor> doctors = new ArrayList<>();
    private static final AtomicInteger nextId = new AtomicInteger(1);

    @Override
    public void addDoctor(Doctor d) {
        if (d.getDepartment() == null) throw new IllegalArgumentException("Doctor must belong to a department");
        if (d.getDoctorId() == 0) d.setDoctorId(nextId.getAndIncrement());
        doctors.add(d);
        System.out.println("Doctor added successfully (ID: " + d.getDoctorId() + ")");
    }

    @Override
    public void updateDoctor(Doctor d) {
        if (getDoctorById(d.getDoctorId()) == null)
            System.out.println("Doctor not found with ID: " + d.getDoctorId());
        else
            System.out.println("Doctor updated successfully");
    }

    @Override
    public void deactivateDoctor(int id) {
        Doctor d = getDoctorById(id);
        if (d == null)
            System.out.println("Doctor not found with ID: " + id);
        else {
            d.setStatus("INACTIVE");
            System.out.println("Doctor deactivated successfully");
        }
    }

    @Override
    public void activateDoctor(int id) {
        Doctor d = getDoctorById(id);
        if (d == null)
            System.out.println("Doctor not found with ID: " + id);
        else {
            d.setStatus("ACTIVE");
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
}
