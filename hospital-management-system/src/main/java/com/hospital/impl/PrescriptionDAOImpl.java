package com.hospital.impl;

import com.hospital.dao.PrescriptionDAO;
import com.hospital.model.Prescription;
import com.hospital.util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class PrescriptionDAOImpl implements PrescriptionDAO {

    // 1. ADD PRESCRIPTION
    @Override
    public void addPrescription(Prescription prescription) {

        String sql = """
                INSERT INTO prescription
                (record_id, medicine_name, dosage, duration)
                VALUES (?, ?, ?, ?)
                """;

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, prescription.getRecordId());
            statement.setString(2, prescription.getMedicineName());
            statement.setString(3, prescription.getDosage());
            statement.setString(4, prescription.getDuration());

            int rowsAffected = statement.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("Prescription added: " + prescription.getMedicineName());
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    // 2. GET PRESCRIPTIONS BY RECORD
    @Override
    public List<Prescription> getPrescriptionsByRecord(int recordId) {

        List<Prescription> prescriptions = new ArrayList<>();

        String sql = """
                SELECT prescription_id, record_id, medicine_name, dosage, duration
                FROM prescription
                WHERE record_id = ?
                """;

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, recordId);

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    prescriptions.add(new Prescription(
                            resultSet.getInt("prescription_id"),
                            resultSet.getInt("record_id"),
                            resultSet.getString("medicine_name"),
                            resultSet.getString("dosage"),
                            resultSet.getString("duration")
                    ));
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return prescriptions;
    }
}