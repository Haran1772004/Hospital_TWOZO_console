package com.hospital.localfunctions;

import com.hospital.model.MedicalRecord;
import java.util.List;

public interface MedicalRecordLF {

    void createMedicalRecord(MedicalRecord record);

    MedicalRecord getRecordById(int recordId);

    List<MedicalRecord> getRecordsByPatient(int patientId);

    List<MedicalRecord> getRecordsByDoctor(int doctorId);

    List<MedicalRecord> getAllRecords();
}