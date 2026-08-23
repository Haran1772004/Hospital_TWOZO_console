package com.hospital.dao;

import com.hospital.model.Payment;
import java.util.List;

public interface PaymentDAO {

    void recordPayment(Payment payment);

    List<Payment> getPaymentsByBill(int billId);

    List<Payment> getAllPayments();
}