package com.hospital.impl;

import com.hospital.dao.BillDAO;
import com.hospital.model.Bill;
import com.hospital.model.Patient;
import com.hospital.util.DBConnection;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BillDAOImpl implements BillDAO {

    private static final String BASE_SELECT = """
            SELECT b.bill_id, b.consultation_charge, b.medicine_charge, b.other_charge,
                   b.total_amount, b.bill_date, b.status AS bill_status,
                   p.patient_id, p.name AS patient_name, p.dob, p.gender,
                   p.phone AS patient_phone, p.email AS patient_email,
                   p.address, p.status AS patient_status
            FROM bill b
            JOIN patient p ON b.patient_id = p.patient_id
            """;

    private Bill mapRow(ResultSet rs) throws SQLException {

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

        return new Bill(
                rs.getInt("bill_id"),
                patient,
                rs.getBigDecimal("consultation_charge"),
                rs.getBigDecimal("medicine_charge"),
                rs.getBigDecimal("other_charge"),
                rs.getBigDecimal("total_amount"),
                rs.getString("bill_date"),
                rs.getString("bill_status")
        );
    }


    // 1. GENERATE BILL
    @Override
    public void generateBill(Bill bill) {

        // Calculate total from the three charge components
        BigDecimal total = bill.getConsultationCharge()
                .add(bill.getMedicineCharge())
                .add(bill.getOtherCharge());
        bill.setTotalAmount(total);

        String sql = """
                INSERT INTO bill
                (patient_id, consultation_charge, medicine_charge, other_charge, total_amount, bill_date, status)
                VALUES (?, ?, ?, ?, ?, ?, ?)
                """;

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            statement.setInt(1, bill.getPatient().getPatientId());
            statement.setBigDecimal(2, bill.getConsultationCharge());
            statement.setBigDecimal(3, bill.getMedicineCharge());
            statement.setBigDecimal(4, bill.getOtherCharge());
            statement.setBigDecimal(5, bill.getTotalAmount());
            statement.setString(6, bill.getBillDate());
            statement.setString(7, "UNPAID");

            int rowsAffected = statement.executeUpdate();

            if (rowsAffected > 0) {
                try (ResultSet keys = statement.getGeneratedKeys()) {
                    if (keys.next()) {
                        bill.setBillId(keys.getInt(1));
                    }
                }
                bill.setStatus("UNPAID");
                System.out.println("Bill generated successfully (ID: " + bill.getBillId()
                        + ", Total: " + bill.getTotalAmount() + ")");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    // 2. UPDATE BILL STATUS
    @Override
    public void updateBillStatus(int billId, String status) {

        String sql = """
                UPDATE bill
                SET status = ?
                WHERE bill_id = ?
                """;

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, status);
            statement.setInt(2, billId);

            statement.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    // 3. GET BILL BY ID
    @Override
    public Bill getBillById(int billId) {

        String sql = BASE_SELECT + " WHERE b.bill_id = ?";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, billId);

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


    // 4. GET BILLS BY PATIENT
    @Override
    public List<Bill> getBillsByPatient(int patientId) {

        List<Bill> bills = new ArrayList<>();

        String sql = BASE_SELECT + " WHERE b.patient_id = ? ORDER BY b.bill_date DESC";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, patientId);

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    bills.add(mapRow(resultSet));
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return bills;
    }


    // 5. GET ALL BILLS
    @Override
    public List<Bill> getAllBills() {

        List<Bill> bills = new ArrayList<>();

        String sql = BASE_SELECT + " ORDER BY b.bill_date DESC";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                bills.add(mapRow(resultSet));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return bills;
    }
}