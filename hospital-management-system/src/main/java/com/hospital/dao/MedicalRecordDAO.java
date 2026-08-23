package com.hospital.dao;

import com.hospital.model.MedicalRecord;
import java.util.List;

public interface MedicalRecordDAO {

    void createMedicalRecord(MedicalRecord record);

    MedicalRecord getRecordById(int recordId);

    List<MedicalRecord> getRecordsByPatient(int patientId);

    List<MedicalRecord> getRecordsByDoctor(int doctorId);

    List<MedicalRecord> getAllRecords();
}