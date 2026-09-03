package com.hospital.impl;

import com.hospital.localfunctions.PaymentLC;
import com.hospital.model.Payment;
import com.hospital.model.Bill;
import com.hospital.util.ValidationUtil;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;

public class PaymentLCImpl implements PaymentLC {
    private static final List<Payment> payments = new CopyOnWriteArrayList<>();
    private static final AtomicInteger nextId = new AtomicInteger(1);
    @Override public void recordPayment(Payment p) {
        if (p == null || !ValidationUtil.isPositiveNumber(p.getAmountPaid()) || !ValidationUtil.isValidDate(p.getPaymentDate())
                || !LocalDate.now().toString().equals(p.getPaymentDate()) || p.getPaymentMethod() == null) {
            throw new IllegalArgumentException("Payment amount, method, and date must be valid; date must be today");
        }
        Bill bill = new BillLCImpl().getBillById(p.getBillId());
        if (bill == null) throw new IllegalArgumentException("Bill does not exist");
        BigDecimal paid = payments.stream().filter(existing -> existing.getBillId() == p.getBillId())
                .map(Payment::getAmountPaid).reduce(BigDecimal.ZERO, BigDecimal::add);
        if (paid.add(p.getAmountPaid()).compareTo(bill.getTotalAmount()) > 0) {
            throw new IllegalArgumentException("Payment exceeds the remaining bill balance");
        }
        if (p.getPaymentId() == 0) p.setPaymentId(nextId.getAndIncrement());
        payments.add(p);
        System.out.println("Payment recorded successfully (ID: " + p.getPaymentId() + ")");
    }
    @Override public List<Payment> getPaymentsByBill(int id) { return payments.stream().filter(p -> p.getBillId() == id).sorted(Comparator.comparing(Payment::getPaymentDate, Comparator.nullsLast(String::compareTo))).toList(); }
    @Override public List<Payment> getAllPayments() { return payments.stream().sorted(Comparator.comparing(Payment::getPaymentDate, Comparator.nullsLast(String::compareTo)).reversed()).toList(); }
}
