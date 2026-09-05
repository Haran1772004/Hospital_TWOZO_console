package com.hospital.localfunctions;

import com.hospital.model.Patient;
import java.util.List;

public interface PatientLF {

    void addPatient(Patient patient);

    void updatePatient(Patient patient);

    void removePatient(int patientId);

    void deactivatePatient(int patientId);

    void activatePatient(int patientId);

    Patient takePatientById(int patientId);

    List<Patient> takeAllPatients();
}
