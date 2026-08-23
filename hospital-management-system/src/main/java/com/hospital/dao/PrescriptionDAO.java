package com.hospital.dao;

import com.hospital.model.Prescription;
import java.util.List;

public interface PrescriptionDAO {

    void addPrescription(Prescription prescription);

    List<Prescription> getPrescriptionsByRecord(int recordId);
}