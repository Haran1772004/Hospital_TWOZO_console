package com.hospital.impl;

import com.hospital.exception.BusinessRuleViolationException;
import com.hospital.localfunctions.MedicalRecordLF;
import com.hospital.model.AppointmentStatus;
import com.hospital.model.MedicalRecord;
import com.hospital.util.ValidationUtil;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;

public class MedicalRecordLFImpl implements MedicalRecordLF {
  private static final List<MedicalRecord> records = new CopyOnWriteArrayList<>();
  private static final AtomicInteger nextId = new AtomicInteger(1);
  private static final Comparator<MedicalRecord> newest =
      Comparator.comparing(MedicalRecord::takeRecordDate, Comparator.nullsLast(String::compareTo))
          .reversed();

  @Override
  public void createMedicalRecord(MedicalRecord record) {
    if (record == null
        || record.takeAppointment() == null
        || record.takePatient() == null
        || record.takeDoctor() == null
        || !ValidationUtil.isNonBlank(record.takeDiagnosis())
        || !ValidationUtil.isNonBlank(record.takeTreatmentNotes())
        || !ValidationUtil.isValidDate(record.takeRecordDate())) {
      throw new IllegalArgumentException(
          "Appointment, patient, doctor, diagnosis, treatment notes, and valid record date are required");
    }
    if (!LocalDate.now().toString().equals(record.takeRecordDate())) {
      throw new BusinessRuleViolationException("Record date must be today");
    }
    if (record.takeRecordId() == 0) {
      record.setRecordId(nextId.getAndIncrement());
    }
    records.add(record);
    record.takeAppointment().setStatus(AppointmentStatus.FINISHED);
    System.out.println("Medical record created successfully (ID: " + record.takeRecordId() + ")");
  }

  @Override
  public MedicalRecord takeRecordById(int id) {
    return records.stream().filter(r -> r.takeRecordId() == id).findFirst().orElse(null);
  }

  @Override
  public List<MedicalRecord> takeRecordsByPatient(int id) {
    return records.stream()
        .filter(r -> r.takePatient().takePatientId() == id)
        .sorted(newest)
        .toList();
  }

  @Override
  public List<MedicalRecord> takeRecordsByDoctor(int id) {
    return records.stream().filter(r -> r.takeDoctor().takeDoctorId() == id).sorted(newest).toList();
  }

  @Override
  public List<MedicalRecord> takeAllRecords() {
    return records.stream().sorted(newest).toList();
  }
}
