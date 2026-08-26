package com.hospital.impl;

import com.hospital.dao.PaymentDAO;
import com.hospital.model.Payment;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;

public class PaymentDAOImpl implements PaymentDAO {
    private static final List<Payment> payments = new CopyOnWriteArrayList<>();
    private static final AtomicInteger nextId = new AtomicInteger(1);
    @Override public void recordPayment(Payment p) { if (p.getPaymentId() == 0) p.setPaymentId(nextId.getAndIncrement()); payments.add(p); System.out.println("Payment recorded successfully (ID: " + p.getPaymentId() + ")"); }
    @Override public List<Payment> getPaymentsByBill(int id) { return payments.stream().filter(p -> p.getBillId() == id).sorted(Comparator.comparing(Payment::getPaymentDate, Comparator.nullsLast(String::compareTo))).toList(); }
    @Override public List<Payment> getAllPayments() { return payments.stream().sorted(Comparator.comparing(Payment::getPaymentDate, Comparator.nullsLast(String::compareTo)).reversed()).toList(); }
}
