package com.hospital.impl;

import com.hospital.dao.PatientDAO;
import com.hospital.model.Patient;
import com.hospital.util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class PatientDAOImpl implements PatientDAO {

    // 1. ADD PATIENT
    @Override
    public void addPatient(Patient patient) {

        String sql = """
                INSERT INTO patient
                (name, dob, gender, phone, email, address, status)
                VALUES (?, ?, ?, ?, ?, ?, ?)
                """;

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setString(1, patient.getName());
            statement.setString(2, patient.getDob());
            statement.setString(3, patient.getGender());
            statement.setString(4, patient.getPhone());
            statement.setString(5, patient.getEmail());
            statement.setString(6, patient.getAddress());
            statement.setString(7, patient.getStatus());

            int rowsAffected = statement.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("Patient added successfully");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    // 2. UPDATE PATIENT
    @Override
    public void updatePatient(Patient patient) {

        String sql = """
                UPDATE patient
                SET name = ?,
                    dob = ?,
                    gender = ?,
                    phone = ?,
                    email = ?,
                    address = ?,
                    status = ?
                WHERE patient_id = ?
                """;

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setString(1, patient.getName());
            statement.setString(2, patient.getDob());
            statement.setString(3, patient.getGender());
            statement.setString(4, patient.getPhone());
            statement.setString(5, patient.getEmail());
            statement.setString(6, patient.getAddress());
            statement.setString(7, patient.getStatus());
            statement.setInt(8, patient.getPatientId());

            int rowsAffected = statement.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("Patient updated successfully");
            } else {
                System.out.println(
                        "Patient not found with ID: " + patient.getPatientId()
                );
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    // 3. DEACTIVATE PATIENT
    @Override
    public void deactivatePatient(int patientId) {

        String sql = """
                UPDATE patient
                SET status = ?
                WHERE patient_id = ?
                """;

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setString(1, "INACTIVE");
            statement.setInt(2, patientId);

            int rowsAffected = statement.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("Patient deactivated successfully");
            } else {
                System.out.println(
                        "Patient not found with ID: " + patientId
                );
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    // 4. GET PATIENT BY ID
    @Override
    public Patient getPatientById(int patientId) {

        String sql = """
                SELECT patient_id,
                       name,
                       dob,
                       gender,
                       phone,
                       email,
                       address,
                       status
                FROM patient
                WHERE patient_id = ?
                """;

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setInt(1, patientId);

            try (ResultSet resultSet = statement.executeQuery()) {

                if (resultSet.next()) {

                    return new Patient(
                            resultSet.getInt("patient_id"),
                            resultSet.getString("name"),
                            resultSet.getString("dob"),
                            resultSet.getString("gender"),
                            resultSet.getString("phone"),
                            resultSet.getString("email"),
                            resultSet.getString("address"),
                            resultSet.getString("status")
                    );
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }


    // 5. GET ALL PATIENTS
    @Override
    public List<Patient> getAllPatients() {

        List<Patient> patients = new ArrayList<>();

        String sql = """
                SELECT patient_id,
                       name,
                       dob,
                       gender,
                       phone,
                       email,
                       address,
                       status
                FROM patient
                """;

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql);
             ResultSet resultSet =
                     statement.executeQuery()) {

            while (resultSet.next()) {

                Patient patient = new Patient(
                        resultSet.getInt("patient_id"),
                        resultSet.getString("name"),
                        resultSet.getString("dob"),
                        resultSet.getString("gender"),
                        resultSet.getString("phone"),
                        resultSet.getString("email"),
                        resultSet.getString("address"),
                        resultSet.getString("status")
                );

                patients.add(patient);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return patients;
    }
}