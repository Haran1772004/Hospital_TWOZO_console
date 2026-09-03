package com.hospital.service;

import com.hospital.localfunctions.BillLC;
import com.hospital.localfunctions.PaymentLC;
import com.hospital.impl.BillLCImpl;
import com.hospital.impl.PaymentLCImpl;
import com.hospital.model.Bill;
import com.hospital.model.Payment;
import com.hospital.util.TablePrinter;
import com.hospital.util.ValidationUtil;
import com.hospital.model.BillStatus;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public class BillingService {

    private final BillLC billLC = new BillLCImpl();
    private final PaymentLC paymentLC = new PaymentLCImpl();

    // Records a payment against a bill, then recalculates and updates that bill's status
    public void makePayment(Payment payment) {

        Bill bill = billLC.getBillById(payment.getBillId());

        if (bill == null) {
            System.out.println("No bill found with ID: " + payment.getBillId());
            return;
        }

        if (BillStatus.PAID == bill.getStatus()) {
            System.out.println("Bill " + bill.getBillId() + " is already fully paid.");
            return;
        }

        if (!ValidationUtil.isPositiveNumber(payment.getAmountPaid()) || !ValidationUtil.isValidDate(payment.getPaymentDate())
                || !LocalDate.now().toString().equals(payment.getPaymentDate()) || payment.getPaymentMethod() == null) {
            System.out.println("Payment amount, method, and date must be valid; date must be today.");
            return;
        }
        BigDecimal remaining = bill.getTotalAmount().subtract(getTotalPaid(bill.getBillId()));
        if (payment.getAmountPaid().compareTo(remaining) > 0) {
            System.out.println("Payment exceeds the remaining bill balance.");
            return;
        }

        // 1. Save the payment row
        paymentLC.recordPayment(payment);

        // 2. Sum ALL payments made so far against this bill (not just this one)
        BigDecimal totalPaid = getTotalPaid(bill.getBillId());

        // 3. Decide the new status by comparing total paid vs bill total
        String newStatus;
        if (totalPaid.compareTo(bill.getTotalAmount()) >= 0) {
            newStatus = "PAID";
        } else if (totalPaid.compareTo(BigDecimal.ZERO) > 0) {
            newStatus = "PARTIAL";
        } else {
            newStatus = "UNPAID";
        }

        billLC.updateBillStatus(bill.getBillId(), newStatus);

        System.out.println("Bill " + bill.getBillId() + " status updated to: " + newStatus
                + " (Paid so far: " + totalPaid + " / " + bill.getTotalAmount() + ")");
    }

    // Sums every payment ever recorded against a given bill
    public BigDecimal getTotalPaid(int billId) {

        List<Payment> payments = paymentLC.getPaymentsByBill(billId);

        BigDecimal total = BigDecimal.ZERO;
        for (Payment p : payments) {
            total = total.add(p.getAmountPaid());
        }
        return total;
    }

    // VIEW PAYMENT HISTORY for one bill (payments + running total + remaining balance)
    public void viewPaymentHistory(int billId) {

        Bill bill = billLC.getBillById(billId);
        if (bill == null) {
            System.out.println("No bill found with ID: " + billId);
            return;
        }

        System.out.println("========== PAYMENT HISTORY - Bill #" + billId + " ==========");
        TablePrinter.printBill(bill);

        List<Payment> payments = paymentLC.getPaymentsByBill(billId);
        System.out.println("\nPayments:");
        TablePrinter.printPayments(payments);

        BigDecimal totalPaid = getTotalPaid(billId);
        BigDecimal remaining = bill.getTotalAmount().subtract(totalPaid);

        System.out.println("\nTotal paid: " + totalPaid);
        System.out.println("Remaining balance: " + remaining);
        System.out.println("===============================================");
    }
}