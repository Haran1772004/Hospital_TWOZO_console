package com.hospital.impl;

import com.hospital.dao.PaymentDAO;
import com.hospital.model.Payment;
import com.hospital.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PaymentDAOImpl implements PaymentDAO {

    // 1. RECORD PAYMENT
    @Override
    public void recordPayment(Payment payment) {

        String sql = """
                INSERT INTO payment
                (bill_id, amount_paid, payment_date, payment_method)
                VALUES (?, ?, ?, ?)
                """;

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            statement.setInt(1, payment.getBillId());
            statement.setBigDecimal(2, payment.getAmountPaid());
            statement.setString(3, payment.getPaymentDate());
            statement.setString(4, payment.getPaymentMethod());

            int rowsAffected = statement.executeUpdate();

            if (rowsAffected > 0) {
                try (ResultSet keys = statement.getGeneratedKeys()) {
                    if (keys.next()) {
                        payment.setPaymentId(keys.getInt(1));
                    }
                }
                System.out.println("Payment recorded successfully (ID: " + payment.getPaymentId() + ")");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    // 2. GET PAYMENTS BY BILL
    @Override
    public List<Payment> getPaymentsByBill(int billId) {

        List<Payment> payments = new ArrayList<>();

        String sql = """
                SELECT payment_id, bill_id, amount_paid, payment_date, payment_method
                FROM payment
                WHERE bill_id = ?
                ORDER BY payment_date
                """;

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, billId);

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    payments.add(new Payment(
                            resultSet.getInt("payment_id"),
                            resultSet.getInt("bill_id"),
                            resultSet.getBigDecimal("amount_paid"),
                            resultSet.getString("payment_date"),
                            resultSet.getString("payment_method")
                    ));
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return payments;
    }


    // 3. GET ALL PAYMENTS
    @Override
    public List<Payment> getAllPayments() {

        List<Payment> payments = new ArrayList<>();

        String sql = """
                SELECT payment_id, bill_id, amount_paid, payment_date, payment_method
                FROM payment
                ORDER BY payment_date DESC
                """;

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                payments.add(new Payment(
                        resultSet.getInt("payment_id"),
                        resultSet.getInt("bill_id"),
                        resultSet.getBigDecimal("amount_paid"),
                        resultSet.getString("payment_date"),
                        resultSet.getString("payment_method")
                ));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return payments;
    }
}