package com.hospital.service;

import com.hospital.dao.BillDAO;
import com.hospital.dao.PaymentDAO;
import com.hospital.impl.BillDAOImpl;
import com.hospital.impl.PaymentDAOImpl;
import com.hospital.model.Bill;
import com.hospital.model.Payment;
import com.hospital.util.TablePrinter;

import java.math.BigDecimal;
import java.util.List;

public class BillingService {

    private final BillDAO billDAO = new BillDAOImpl();
    private final PaymentDAO paymentDAO = new PaymentDAOImpl();

    // Records a payment against a bill, then recalculates and updates that bill's status
    public void makePayment(Payment payment) {

        Bill bill = billDAO.getBillById(payment.getBillId());

        if (bill == null) {
            System.out.println("No bill found with ID: " + payment.getBillId());
            return;
        }

        if ("PAID".equals(bill.getStatus())) {
            System.out.println("Bill " + bill.getBillId() + " is already fully paid.");
            return;
        }

        // 1. Save the payment row
        paymentDAO.recordPayment(payment);

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

        billDAO.updateBillStatus(bill.getBillId(), newStatus);

        System.out.println("Bill " + bill.getBillId() + " status updated to: " + newStatus
                + " (Paid so far: " + totalPaid + " / " + bill.getTotalAmount() + ")");
    }

    // Sums every payment ever recorded against a given bill
    public BigDecimal getTotalPaid(int billId) {

        List<Payment> payments = paymentDAO.getPaymentsByBill(billId);

        BigDecimal total = BigDecimal.ZERO;
        for (Payment p : payments) {
            total = total.add(p.getAmountPaid());
        }
        return total;
    }

    // VIEW PAYMENT HISTORY for one bill (payments + running total + remaining balance)
    public void viewPaymentHistory(int billId) {

        Bill bill = billDAO.getBillById(billId);
        if (bill == null) {
            System.out.println("No bill found with ID: " + billId);
            return;
        }

        System.out.println("========== PAYMENT HISTORY - Bill #" + billId + " ==========");
        TablePrinter.printBill(bill);

        List<Payment> payments = paymentDAO.getPaymentsByBill(billId);
        System.out.println("\nPayments:");
        TablePrinter.printPayments(payments);

        BigDecimal totalPaid = getTotalPaid(billId);
        BigDecimal remaining = bill.getTotalAmount().subtract(totalPaid);

        System.out.println("\nTotal paid: " + totalPaid);
        System.out.println("Remaining balance: " + remaining);
        System.out.println("===============================================");
    }
}