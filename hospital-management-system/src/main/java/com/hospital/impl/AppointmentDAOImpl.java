package com.hospital.impl;

import com.hospital.dao.AppointmentDAO;
import com.hospital.model.Appointment;
import com.hospital.model.Department;
import com.hospital.model.Doctor;
import com.hospital.model.Patient;
import com.hospital.util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class AppointmentDAOImpl implements AppointmentDAO {

    // Reused by every method that needs full Patient + Doctor + Department details
    private static final String BASE_SELECT = """
            SELECT a.appointment_id,
                   a.appointment_date,
                   a.appointment_time,
                   a.status AS appointment_status,
                   p.patient_id, p.name AS patient_name, p.dob, p.gender,
                   p.phone AS patient_phone, p.email AS patient_email,
                   p.address, p.status AS patient_status,
                   doc.doctor_id, doc.name AS doctor_name, doc.specialization,
                   doc.phone AS doctor_phone, doc.email AS doctor_email,
                   doc.status AS doctor_status,
                   dept.department_id, dept.name AS department_name,
                   dept.description, dept.status AS department_status
            FROM appointment a
            JOIN patient p ON a.patient_id = p.patient_id
            JOIN doctor doc ON a.doctor_id = doc.doctor_id
            JOIN department dept ON doc.department_id = dept.department_id
            """;

    // Builds a full Appointment (with nested Patient, Doctor, Department) from one ResultSet row
    private Appointment mapRow(ResultSet rs) throws SQLException {

        Department department = new Department(
                rs.getInt("department_id"),
                rs.getString("department_name"),
                rs.getString("description"),
                rs.getString("department_status")
        );

        Doctor doctor = new Doctor(
                rs.getInt("doctor_id"),
                rs.getString("doctor_name"),
                rs.getString("specialization"),
                rs.getString("doctor_phone"),
                rs.getString("doctor_email"),
                department,
                rs.getString("doctor_status")
        );

        Patient patient = new Patient(
                rs.getInt("patient_id"),
                rs.getString("patient_name"),
                rs.getString("dob"),
                rs.getString("gender"),
                rs.getString("patient_phone"),
                rs.getString("patient_email"),
                rs.getString("address"),
                rs.getString("patient_status")
        );

        return new Appointment(
                rs.getInt("appointment_id"),
                patient,
                doctor,
                rs.getString("appointment_date"),
                rs.getString("appointment_time"),
                rs.getString("appointment_status")
        );
    }


    // 1. BOOK APPOINTMENT
    @Override
    public void bookAppointment(Appointment appointment) {

        if (!isDoctorAvailable(
                appointment.getDoctor().getDoctorId(),
                appointment.getAppointmentDate(),
                appointment.getAppointmentTime())) {

            System.out.println("Doctor is not available at this date/time. Booking cancelled.");
            return;
        }

        String sql = """
                INSERT INTO appointment
                (patient_id, doctor_id, appointment_date, appointment_time, status)
                VALUES (?, ?, ?, ?, ?)
                """;

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, appointment.getPatient().getPatientId());
            statement.setInt(2, appointment.getDoctor().getDoctorId());
            statement.setString(3, appointment.getAppointmentDate());
            statement.setString(4, appointment.getAppointmentTime());
            statement.setString(5, "SCHEDULED");

            int rowsAffected = statement.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("Appointment booked successfully");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    // 2. CANCEL APPOINTMENT
    @Override
    public void cancelAppointment(int appointmentId) {

        String sql = """
                UPDATE appointment
                SET status = ?
                WHERE appointment_id = ?
                """;

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, "CANCELLED");
            statement.setInt(2, appointmentId);

            int rowsAffected = statement.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("Appointment cancelled successfully");
            } else {
                System.out.println("Appointment not found with ID: " + appointmentId);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    // 3. CHECK DOCTOR AVAILABILITY
    @Override
    public boolean isDoctorAvailable(int doctorId, String date, String time) {

        String sql = """
                SELECT COUNT(*) AS total
                FROM appointment
                WHERE doctor_id = ?
                  AND appointment_date = ?
                  AND appointment_time = ?
                  AND status = 'SCHEDULED'
                """;

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, doctorId);
            statement.setString(2, date);
            statement.setString(3, time);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getInt("total") == 0;
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }


    // 4. GET APPOINTMENTS BY PATIENT
    @Override
    public List<Appointment> getAppointmentsByPatient(int patientId) {

        List<Appointment> appointments = new ArrayList<>();

        String sql = BASE_SELECT + " WHERE a.patient_id = ? ORDER BY a.appointment_date, a.appointment_time";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, patientId);

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    appointments.add(mapRow(resultSet));
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return appointments;
    }


    // 5. GET APPOINTMENTS BY DOCTOR
    @Override
    public List<Appointment> getAppointmentsByDoctor(int doctorId) {

        List<Appointment> appointments = new ArrayList<>();

        String sql = BASE_SELECT + " WHERE a.doctor_id = ? ORDER BY a.appointment_date, a.appointment_time";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, doctorId);

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    appointments.add(mapRow(resultSet));
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return appointments;
    }


    // 6. GET TODAY'S APPOINTMENTS
    @Override
    public List<Appointment> getTodaysAppointments() {

        List<Appointment> appointments = new ArrayList<>();

        String sql = BASE_SELECT + " WHERE a.appointment_date = CURDATE() ORDER BY a.appointment_time";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                appointments.add(mapRow(resultSet));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return appointments;
    }


    // 7. GET ALL APPOINTMENTS
    @Override
    public List<Appointment> getAllAppointments() {

        List<Appointment> appointments = new ArrayList<>();

        String sql = BASE_SELECT + " ORDER BY a.appointment_date, a.appointment_time";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                appointments.add(mapRow(resultSet));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return appointments;
    }
}