package com.hospital.impl;

import com.hospital.dao.PrescriptionDAO;
import com.hospital.model.Prescription;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;

public class PrescriptionDAOImpl implements PrescriptionDAO {
    private static final List<Prescription> prescriptions = new CopyOnWriteArrayList<>();
    private static final AtomicInteger nextId = new AtomicInteger(1);
    @Override public void addPrescription(Prescription p) { if (p.getPrescriptionId() == 0) p.setPrescriptionId(nextId.getAndIncrement()); prescriptions.add(p); System.out.println("Prescription added: " + p.getMedicineName()); }
    @Override public List<Prescription> getPrescriptionsByRecord(int id) { return prescriptions.stream().filter(p -> p.getRecordId() == id).toList(); }
}
