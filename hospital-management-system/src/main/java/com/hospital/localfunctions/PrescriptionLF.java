package com.hospital.localfunctions;

import com.hospital.model.Prescription;
import java.util.List;

public interface PrescriptionLF {

    void addPrescription(Prescription prescription);

    List<Prescription> getPrescriptionsByRecord(int recordId);
}