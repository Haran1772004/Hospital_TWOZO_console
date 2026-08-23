package com.hospital.impl;

import com.hospital.dao.MedicalRecordDAO;
import com.hospital.model.*;
import com.hospital.util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class MedicalRecordDAOImpl implements MedicalRecordDAO {

    private static final String BASE_SELECT = """
            SELECT mr.record_id, mr.diagnosis, mr.treatment_notes, mr.record_date,
                   a.appointment_id, a.appointment_date, a.appointment_time, a.status AS appointment_status,
                   p.patient_id, p.name AS patient_name, p.dob, p.gender,
                   p.phone AS patient_phone, p.email AS patient_email,
                   p.address, p.status AS patient_status,
                   doc.doctor_id, doc.name AS doctor_name, doc.specialization,
                   doc.phone AS doctor_phone, doc.email AS doctor_email,
                   doc.status AS doctor_status,
                   dept.department_id, dept.name AS department_name,
                   dept.description, dept.status AS department_status
            FROM medical_record mr
            JOIN appointment a ON mr.appointment_id = a.appointment_id
            JOIN patient p ON mr.patient_id = p.patient_id
            JOIN doctor doc ON mr.doctor_id = doc.doctor_id
            JOIN department dept ON doc.department_id = dept.department_id
            """;

    private MedicalRecord mapRow(ResultSet rs) throws SQLException {

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

        Appointment appointment = new Appointment(
                rs.getInt("appointment_id"),
                patient,
                doctor,
                rs.getString("appointment_date"),
                rs.getString("appointment_time"),
                rs.getString("appointment_status")
        );

        return new MedicalRecord(
                rs.getInt("record_id"),
                appointment,
                patient,
                doctor,
                rs.getString("diagnosis"),
                rs.getString("treatment_notes"),
                rs.getString("record_date")
        );
    }


    // 1. CREATE MEDICAL RECORD
    @Override
    public void createMedicalRecord(MedicalRecord record) {

        String sql = """
                INSERT INTO medical_record
                (appointment_id, patient_id, doctor_id, diagnosis, treatment_notes, record_date)
                VALUES (?, ?, ?, ?, ?, ?)
                """;

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            statement.setInt(1, record.getAppointment().getAppointmentId());
            statement.setInt(2, record.getPatient().getPatientId());
            statement.setInt(3, record.getDoctor().getDoctorId());
            statement.setString(4, record.getDiagnosis());
            statement.setString(5, record.getTreatmentNotes());
            statement.setString(6, record.getRecordDate());

            int rowsAffected = statement.executeUpdate();

            if (rowsAffected > 0) {
                try (ResultSet keys = statement.getGeneratedKeys()) {
                    if (keys.next()) {
                        record.setRecordId(keys.getInt(1));
                    }
                }
                System.out.println("Medical record created successfully (ID: " + record.getRecordId() + ")");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    // 2. GET RECORD BY ID
    @Override
    public MedicalRecord getRecordById(int recordId) {

        String sql = BASE_SELECT + " WHERE mr.record_id = ?";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, recordId);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return mapRow(resultSet);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }


    // 3. GET RECORDS BY PATIENT
    @Override
    public List<MedicalRecord> getRecordsByPatient(int patientId) {

        List<MedicalRecord> records = new ArrayList<>();

        String sql = BASE_SELECT + " WHERE mr.patient_id = ? ORDER BY mr.record_date DESC";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, patientId);

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    records.add(mapRow(resultSet));
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return records;
    }


    // 4. GET RECORDS BY DOCTOR
    @Override
    public List<MedicalRecord> getRecordsByDoctor(int doctorId) {

        List<MedicalRecord> records = new ArrayList<>();

        String sql = BASE_SELECT + " WHERE mr.doctor_id = ? ORDER BY mr.record_date DESC";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, doctorId);

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    records.add(mapRow(resultSet));
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return records;
    }


    // 5. GET ALL RECORDS
    @Override
    public List<MedicalRecord> getAllRecords() {

        List<MedicalRecord> records = new ArrayList<>();

        String sql = BASE_SELECT + " ORDER BY mr.record_date DESC";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                records.add(mapRow(resultSet));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return records;
    }
}