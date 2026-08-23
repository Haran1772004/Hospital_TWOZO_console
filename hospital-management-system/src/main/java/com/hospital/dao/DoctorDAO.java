package com.hospital.dao;

import com.hospital.model.Doctor;
import java.util.List;

public interface DoctorDAO {

    void addDoctor(Doctor doctor);

    void updateDoctor(Doctor doctor);

    void deactivateDoctor(int doctorId);

    Doctor getDoctorById(int doctorId);

    List<Doctor> getAllDoctors();

}