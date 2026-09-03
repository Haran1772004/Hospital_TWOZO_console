package com.hospital.localfunctions;

import com.hospital.model.Appointment;
import java.util.List;

public interface AppointmentLC {

    void bookAppointment(Appointment appointment);

    void cancelAppointment(int appointmentId);

    boolean isDoctorAvailable(int doctorId, String date, String time);

    List<Appointment> getAppointmentsByPatient(int patientId);

    List<Appointment> getAppointmentsByDoctor(int doctorId);

    List<Appointment> getTodaysAppointments();

    List<Appointment> getAllAppointments();
}