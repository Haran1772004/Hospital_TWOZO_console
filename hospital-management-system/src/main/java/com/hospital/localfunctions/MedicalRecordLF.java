package com.hospital.localfunctions;

import com.hospital.model.MedicalRecord;
import java.util.List;

public interface MedicalRecordLF {

    void createMedicalRecord(MedicalRecord record);

    MedicalRecord takeRecordById(int recordId);

    List<MedicalRecord> takeRecordsByPatient(int patientId);

    List<MedicalRecord> takeRecordsByDoctor(int doctorId);

    List<MedicalRecord> takeAllRecords();
}
