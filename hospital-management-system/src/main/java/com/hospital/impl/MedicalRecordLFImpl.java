package com.hospital.impl;

import com.hospital.localfunctions.MedicalRecordLF;
import com.hospital.model.MedicalRecord;
import com.hospital.model.AppointmentStatus;
import com.hospital.util.ValidationUtil;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;

public class MedicalRecordLFImpl implements MedicalRecordLF {
    private static final List<MedicalRecord> records = new CopyOnWriteArrayList<>();
    private static final AtomicInteger nextId = new AtomicInteger(1);
    private static final Comparator<MedicalRecord> newest = Comparator.comparing(MedicalRecord::getRecordDate, Comparator.nullsLast(String::compareTo)).reversed();
    @Override public void createMedicalRecord(MedicalRecord record) {
        if (record == null || record.getAppointment() == null || record.getPatient() == null
                || record.getDoctor() == null || !ValidationUtil.isNonBlank(record.getDiagnosis())
                || !ValidationUtil.isNonBlank(record.getTreatmentNotes())
                || !ValidationUtil.isValidDate(record.getRecordDate())) {
            throw new IllegalArgumentException("Appointment, patient, doctor, diagnosis, treatment notes, and valid record date are required");
        }
        if (!LocalDate.now().toString().equals(record.getRecordDate())) {
            throw new IllegalArgumentException("Record date must be today");
        }
        if (record.getRecordId() == 0) record.setRecordId(nextId.getAndIncrement());
        records.add(record);
        record.getAppointment().setStatus(AppointmentStatus.FINISHED);
        System.out.println("Medical record created successfully (ID: " + record.getRecordId() + ")");
    }
    @Override public MedicalRecord getRecordById(int id) { return records.stream().filter(r -> r.getRecordId() == id).findFirst().orElse(null); }
    @Override public List<MedicalRecord> getRecordsByPatient(int id) { return records.stream().filter(r -> r.getPatient().getPatientId() == id).sorted(newest).toList(); }
    @Override public List<MedicalRecord> getRecordsByDoctor(int id) { return records.stream().filter(r -> r.getDoctor().getDoctorId() == id).sorted(newest).toList(); }
    @Override public List<MedicalRecord> getAllRecords() { return records.stream().sorted(newest).toList(); }
}
