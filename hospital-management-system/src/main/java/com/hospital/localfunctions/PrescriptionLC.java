package com.hospital.localfunctions;

import com.hospital.model.Prescription;
import java.util.List;

public interface PrescriptionLC {

    void addPrescription(Prescription prescription);

    List<Prescription> getPrescriptionsByRecord(int recordId);
}