package com.hospital.localfunctions;

import com.hospital.model.Payment;
import java.util.List;

public interface PaymentLF {

    void recordPayment(Payment payment);

    List<Payment> getPaymentsByBill(int billId);

    List<Payment> getAllPayments();
}