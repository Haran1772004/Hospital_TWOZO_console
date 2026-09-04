package com.hospital.service;

import com.hospital.impl.BillLFImpl;
import com.hospital.impl.PaymentLFImpl;
import com.hospital.localfunctions.BillLF;
import com.hospital.localfunctions.PaymentLF;
import com.hospital.model.Bill;
import com.hospital.model.BillStatus;
import com.hospital.model.Payment;
import com.hospital.util.TablePrinter;
import com.hospital.util.ValidationUtil;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public class BillingService {

  private final BillLF billLF = new BillLFImpl();
  private final PaymentLF paymentLF = new PaymentLFImpl();

  public void makePayment(Payment payment) {

    Bill bill = billLF.getBillById(payment.getBillId());

    if (bill == null) {
      System.out.println("No bill found with ID: " + payment.getBillId());
      return;
    }

    if (BillStatus.PAID == bill.getStatus()) {
      System.out.println("Bill " + bill.getBillId() + " is already fully paid.");
      return;
    }

    if (!ValidationUtil.isPositiveNumber(payment.getAmountPaid())
        || !ValidationUtil.isValidDate(payment.getPaymentDate())
        || !LocalDate.now().toString().equals(payment.getPaymentDate())
        || payment.getPaymentMethod() == null) {
      System.out.println("Payment amount, method, and date must be valid; date must be today.");
      return;
    }
    BigDecimal remaining = bill.getTotalAmount().subtract(getTotalPaid(bill.getBillId()));
    if (payment.getAmountPaid().compareTo(remaining) > 0) {
      System.out.println("Payment exceeds the remaining bill balance.");
      return;
    }

    paymentLF.recordPayment(payment);

    BigDecimal totalPaid = getTotalPaid(bill.getBillId());

    String newStatus;
    if (totalPaid.compareTo(bill.getTotalAmount()) >= 0) {
      newStatus = "PAID";
    } else if (totalPaid.compareTo(BigDecimal.ZERO) > 0) {
      newStatus = "PARTIAL";
    } else {
      newStatus = "UNPAID";
    }

    billLF.updateBillStatus(bill.getBillId(), newStatus);

    System.out.println(
        "Bill "
            + bill.getBillId()
            + " status updated to: "
            + newStatus
            + " (Paid so far: "
            + totalPaid
            + " / "
            + bill.getTotalAmount()
            + ")");
  }

  public BigDecimal getTotalPaid(int billId) {

    List<Payment> payments = paymentLF.getPaymentsByBill(billId);

    BigDecimal total = BigDecimal.ZERO;
    for (Payment p : payments) {
      total = total.add(p.getAmountPaid());
    }
    return total;
  }

  public void viewPaymentHistory(int billId) {

    Bill bill = billLF.getBillById(billId);
    if (bill == null) {
      System.out.println("No bill found with ID: " + billId);
      return;
    }

    System.out.println("========== PAYMENT HISTORY - Bill #" + billId + " ==========");
    TablePrinter.printBill(bill);

    List<Payment> payments = paymentLF.getPaymentsByBill(billId);
    System.out.println("\nPayments:");
    TablePrinter.printPayments(payments);

    BigDecimal totalPaid = getTotalPaid(billId);
    BigDecimal remaining = bill.getTotalAmount().subtract(totalPaid);

    System.out.println("\nTotal paid: " + totalPaid);
    System.out.println("Remaining balance: " + remaining);
    System.out.println("===============================================");
  }
}
