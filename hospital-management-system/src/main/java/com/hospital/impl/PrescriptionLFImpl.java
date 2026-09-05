package com.hospital.impl;

import com.hospital.localfunctions.PrescriptionLF;
import com.hospital.model.Prescription;
import com.hospital.util.ValidationUtil;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;

public class PrescriptionLFImpl implements PrescriptionLF {
  private static final List<Prescription> prescriptions = new CopyOnWriteArrayList<>();
  private static final AtomicInteger nextId = new AtomicInteger(1);

  @Override
  public void addPrescription(Prescription p) {
    if (p == null
        || p.takeRecordId() <= 0
        || !ValidationUtil.isNonBlank(p.takeMedicineName())
        || !ValidationUtil.isNonBlank(p.takeDosage())
        || !ValidationUtil.isNonBlank(p.takeDuration())) {
      throw new IllegalArgumentException(
          "Medical record, medicine name, dosage, and duration are required");
    }
    if (p.takePrescriptionId() == 0) {
      p.setPrescriptionId(nextId.getAndIncrement());
    }
    prescriptions.add(p);
    System.out.println("Prescription added: " + p.takeMedicineName());
  }

  @Override
  public List<Prescription> takePrescriptionsByRecord(int id) {
    return prescriptions.stream().filter(p -> p.takeRecordId() == id).toList();
  }
}
