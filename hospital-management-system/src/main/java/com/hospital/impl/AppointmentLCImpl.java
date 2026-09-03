package com.hospital.impl;

import com.hospital.localfunctions.AppointmentLC;
import com.hospital.model.Appointment;
import com.hospital.model.Doctor;
import com.hospital.model.Patient;
import com.hospital.model.AccountStatus;
import com.hospital.model.AppointmentStatus;
import com.hospital.util.ValidationUtil;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;

public class AppointmentLCImpl implements AppointmentLC {
    private static final List<Appointment> appointments = new CopyOnWriteArrayList<>();
    private static final AtomicInteger nextId = new AtomicInteger(1);
    private static final Comparator<Appointment> byDateTime = Comparator.comparing(Appointment::getAppointmentDate, Comparator.nullsLast(String::compareTo)).thenComparing(Appointment::getAppointmentTime, Comparator.nullsLast(String::compareTo));
    @Override public void bookAppointment(Appointment a) {
        if (a == null || a.getPatient() == null || a.getDoctor() == null) throw new IllegalArgumentException("Patient and doctor are required");
        Patient patient = new PatientLCImpl().getPatientById(a.getPatient().getPatientId());
        Doctor doctor = new DoctorLCImpl().getDoctorById(a.getDoctor().getDoctorId());
        if (patient == null || doctor == null) throw new IllegalArgumentException("Patient or doctor does not exist");
        if (AccountStatus.ACTIVE != patient.getStatus() || AccountStatus.ACTIVE != doctor.getStatus()) throw new IllegalArgumentException("Patient and doctor must be active");
        LocalDate date = ValidationUtil.parseDate(a.getAppointmentDate());
        LocalTime time = ValidationUtil.parseTime(a.getAppointmentTime());
        if (date == null || time == null) throw new IllegalArgumentException("Invalid appointment date or time");
        if (LocalDateTime.of(date, time).isBefore(LocalDateTime.now())) throw new IllegalArgumentException("Appointment cannot be in the past");
        String normalizedTime = time.toString().length() == 5 ? time.toString() + ":00" : time.toString();
        if (!isDoctorAvailable(doctor.getDoctorId(), date.toString(), normalizedTime)
                || appointments.stream().anyMatch(existing -> AppointmentStatus.SCHEDULED == existing.getStatus()
                && existing.getPatient().getPatientId() == patient.getPatientId()
                && date.toString().equals(existing.getAppointmentDate())
                && normalizedTime.equals(normalizeTime(existing.getAppointmentTime())))) {
            throw new IllegalArgumentException("Doctor or patient already has an appointment at that time");
        }
        a.setAppointmentDate(date.toString());
        a.setAppointmentTime(normalizedTime);
        if (a.getAppointmentId() == 0) a.setAppointmentId(nextId.getAndIncrement());
        appointments.add(a);
        System.out.println("Appointment booked successfully (ID: " + a.getAppointmentId() + ")");
    }
    @Override public void cancelAppointment(int id) { Appointment a = appointments.stream().filter(x -> x.getAppointmentId() == id).findFirst().orElse(null); if (a == null) System.out.println("Appointment not found with ID: " + id); else { a.setStatus(AppointmentStatus.CANCELLED); System.out.println("Appointment cancelled successfully"); } }
    @Override public boolean isDoctorAvailable(int doctorId, String date, String time) { return appointments.stream().noneMatch(a -> a.getDoctor().getDoctorId() == doctorId && date.equals(a.getAppointmentDate()) && normalizeTime(time).equals(normalizeTime(a.getAppointmentTime())) && AppointmentStatus.SCHEDULED == a.getStatus()); }
    @Override public List<Appointment> getAppointmentsByPatient(int id) { return appointments.stream().filter(a -> a.getPatient().getPatientId() == id).sorted(byDateTime).toList(); }
    @Override public List<Appointment> getAppointmentsByDoctor(int id) { return appointments.stream().filter(a -> a.getDoctor().getDoctorId() == id).sorted(byDateTime).toList(); }
    @Override public List<Appointment> getTodaysAppointments() { return appointments.stream().filter(a -> LocalDate.now().toString().equals(a.getAppointmentDate())).sorted(Comparator.comparing(Appointment::getAppointmentTime, Comparator.nullsLast(String::compareTo))).toList(); }
    @Override public List<Appointment> getAllAppointments() { return appointments.stream().sorted(byDateTime).toList(); }
    private String normalizeTime(String value) {
        LocalTime parsed = ValidationUtil.parseTime(value);
        return parsed == null ? value : parsed.toString().length() == 5 ? parsed.toString() + ":00" : parsed.toString();
    }
}
