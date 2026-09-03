package com.hospital.localfunctions;

import com.hospital.model.Doctor;
import java.util.List;

public interface DoctorLC {

    void addDoctor(Doctor doctor);

    void updateDoctor(Doctor doctor);

    void deactivateDoctor(int doctorId);

    void activateDoctor(int doctorId);

    Doctor getDoctorById(int doctorId);

    List<Doctor> getAllDoctors();
}
