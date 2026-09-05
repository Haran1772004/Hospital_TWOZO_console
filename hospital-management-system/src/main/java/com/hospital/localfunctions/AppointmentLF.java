package com.hospital.localfunctions;

import com.hospital.model.Appointment;
import java.util.List;

public interface AppointmentLF {

    void bookAppointment(Appointment appointment);

    void cancelAppointment(int appointmentId);

    boolean isDoctorAvailable(int doctorId, String date, String time);

    List<Appointment> takeAppointmentsByPatient(int patientId);

    List<Appointment> takeAppointmentsByDoctor(int doctorId);

    List<Appointment> takeTodaysAppointments();

    List<Appointment> takeAllAppointments();
}
