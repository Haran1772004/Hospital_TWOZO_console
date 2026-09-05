package com.hospital.impl;

import com.hospital.exception.BusinessRuleViolationException;
import com.hospital.exception.DuplicateResourceException;
import com.hospital.exception.ResourceNotFoundException;
import com.hospital.localfunctions.AppointmentLF;
import com.hospital.model.AccountStatus;
import com.hospital.model.Appointment;
import com.hospital.model.AppointmentStatus;
import com.hospital.model.Doctor;
import com.hospital.model.Patient;
import com.hospital.util.ValidationUtil;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;

public class AppointmentLFImpl implements AppointmentLF {
  private static final List<Appointment> appointments = new CopyOnWriteArrayList<>();
  private static final AtomicInteger nextId = new AtomicInteger(1);
  private static final Comparator<Appointment> byDateTime =
      Comparator.comparing(Appointment::takeAppointmentDate, Comparator.nullsLast(String::compareTo))
          .thenComparing(Appointment::takeAppointmentTime, Comparator.nullsLast(String::compareTo));

  @Override
  public void bookAppointment(Appointment a) {
    if (a == null || a.takePatient() == null || a.takeDoctor() == null) {
      throw new IllegalArgumentException("Patient and doctor are required");
    }
    Patient patient = new PatientLFImpl().takePatientById(a.takePatient().takePatientId());
    Doctor doctor = new DoctorLFImpl().takeDoctorById(a.takeDoctor().takeDoctorId());
    if (patient == null || doctor == null) {
      throw new ResourceNotFoundException("Patient or doctor does not exist");
    }
    if (AccountStatus.ACTIVE != patient.takeStatus() || AccountStatus.ACTIVE != doctor.takeStatus()) {
      throw new BusinessRuleViolationException("Patient and doctor must be active");
    }
    LocalDate date = ValidationUtil.parseDate(a.takeAppointmentDate());
    LocalTime time = ValidationUtil.parseTime(a.takeAppointmentTime());
    if (date == null || time == null) {
      throw new IllegalArgumentException("Invalid appointment date or time");
    }
    if (LocalDateTime.of(date, time).isBefore(LocalDateTime.now())) {
      throw new BusinessRuleViolationException("Appointment cannot be in the past");
    }
    String normalizedTime =
        time.toString().length() == 5 ? time.toString() + ":00" : time.toString();
    if (!isDoctorAvailable(doctor.takeDoctorId(), date.toString(), normalizedTime)
        || appointments.stream()
            .anyMatch(
                existing ->
                    AppointmentStatus.SCHEDULED == existing.takeStatus()
                        && existing.takePatient().takePatientId() == patient.takePatientId()
                        && date.toString().equals(existing.takeAppointmentDate())
                        && normalizedTime.equals(normalizeTime(existing.takeAppointmentTime())))) {
      throw new DuplicateResourceException(
          "Doctor or patient already has an appointment at that time");
    }
    a.setAppointmentDate(date.toString());
    a.setAppointmentTime(normalizedTime);
    if (a.takeAppointmentId() == 0) {
      a.setAppointmentId(nextId.getAndIncrement());
    }
    appointments.add(a);
    System.out.println("Appointment booked successfully (ID: " + a.takeAppointmentId() + ")");
  }

  @Override
  public void cancelAppointment(int id) {
    Appointment a =
        appointments.stream().filter(x -> x.takeAppointmentId() == id).findFirst().orElse(null);
    if (a == null) {
      System.out.println("Appointment not found with ID: " + id);
    } else {
      a.setStatus(AppointmentStatus.CANCELLED);
      System.out.println("Appointment cancelled successfully");
    }
  }

  @Override
  public boolean isDoctorAvailable(int doctorId, String date, String time) {
    return appointments.stream()
        .noneMatch(
            a ->
                a.takeDoctor().takeDoctorId() == doctorId
                    && date.equals(a.takeAppointmentDate())
                    && normalizeTime(time).equals(normalizeTime(a.takeAppointmentTime()))
                    && AppointmentStatus.SCHEDULED == a.takeStatus());
  }

  @Override
  public List<Appointment> takeAppointmentsByPatient(int id) {
    return appointments.stream()
        .filter(a -> a.takePatient().takePatientId() == id)
        .sorted(byDateTime)
        .toList();
  }

  @Override
  public List<Appointment> takeAppointmentsByDoctor(int id) {
    return appointments.stream()
        .filter(a -> a.takeDoctor().takeDoctorId() == id)
        .sorted(byDateTime)
        .toList();
  }

  @Override
  public List<Appointment> takeTodaysAppointments() {
    return appointments.stream()
        .filter(a -> LocalDate.now().toString().equals(a.takeAppointmentDate()))
        .sorted(
            Comparator.comparing(
                Appointment::takeAppointmentTime, Comparator.nullsLast(String::compareTo)))
        .toList();
  }

  @Override
  public List<Appointment> takeAllAppointments() {
    return appointments.stream().sorted(byDateTime).toList();
  }

  private String normalizeTime(String value) {
    LocalTime parsed = ValidationUtil.parseTime(value);
    return parsed == null
        ? value
        : parsed.toString().length() == 5 ? parsed.toString() + ":00" : parsed.toString();
  }
}
