package com.hospital.dao;

import com.hospital.model.Patient;
import java.util.List;

public interface PatientDAO {

    void addPatient(Patient patient);

    void updatePatient(Patient patient);

    void deactivatePatient(int patientId);

    Patient getPatientById(int patientId);

    List<Patient> getAllPatients();
}