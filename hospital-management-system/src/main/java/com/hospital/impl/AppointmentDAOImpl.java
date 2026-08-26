package com.hospital.impl;

import com.hospital.dao.AppointmentDAO;
import com.hospital.model.Appointment;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;

public class AppointmentDAOImpl implements AppointmentDAO {
    private static final List<Appointment> appointments = new CopyOnWriteArrayList<>();
    private static final AtomicInteger nextId = new AtomicInteger(1);
    private static final Comparator<Appointment> byDateTime = Comparator.comparing(Appointment::getAppointmentDate, Comparator.nullsLast(String::compareTo)).thenComparing(Appointment::getAppointmentTime, Comparator.nullsLast(String::compareTo));
    @Override public void bookAppointment(Appointment a) { if (!isDoctorAvailable(a.getDoctor().getDoctorId(), a.getAppointmentDate(), a.getAppointmentTime())) { System.out.println("Doctor is not available at that time."); return; } if (a.getAppointmentId() == 0) a.setAppointmentId(nextId.getAndIncrement()); appointments.add(a); System.out.println("Appointment booked successfully (ID: " + a.getAppointmentId() + ")"); }
    @Override public void cancelAppointment(int id) { Appointment a = appointments.stream().filter(x -> x.getAppointmentId() == id).findFirst().orElse(null); if (a == null) System.out.println("Appointment not found with ID: " + id); else { a.setStatus("CANCELLED"); System.out.println("Appointment cancelled successfully"); } }
    @Override public boolean isDoctorAvailable(int doctorId, String date, String time) { return appointments.stream().noneMatch(a -> a.getDoctor().getDoctorId() == doctorId && date.equals(a.getAppointmentDate()) && time.equals(a.getAppointmentTime()) && "SCHEDULED".equals(a.getStatus())); }
    @Override public List<Appointment> getAppointmentsByPatient(int id) { return appointments.stream().filter(a -> a.getPatient().getPatientId() == id).sorted(byDateTime).toList(); }
    @Override public List<Appointment> getAppointmentsByDoctor(int id) { return appointments.stream().filter(a -> a.getDoctor().getDoctorId() == id).sorted(byDateTime).toList(); }
    @Override public List<Appointment> getTodaysAppointments() { return appointments.stream().filter(a -> LocalDate.now().toString().equals(a.getAppointmentDate())).sorted(Comparator.comparing(Appointment::getAppointmentTime, Comparator.nullsLast(String::compareTo))).toList(); }
    @Override public List<Appointment> getAllAppointments() { return appointments.stream().sorted(byDateTime).toList(); }
}
