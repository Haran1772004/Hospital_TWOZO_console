package com.hospital.model;

import java.math.BigDecimal;

public class Payment {

    private int paymentId;
    private int billId;
    private BigDecimal amountPaid;
    private String paymentDate;
    private PaymentMethod paymentMethod;

    public Payment() {
    }

    public Payment(int paymentId, int billId, BigDecimal amountPaid,
                   String paymentDate, PaymentMethod paymentMethod) {
        this.paymentId = paymentId;
        this.billId = billId;
        this.amountPaid = amountPaid;
        this.paymentDate = paymentDate;
        this.paymentMethod = paymentMethod;
    }

    public Payment(int paymentId, int billId, BigDecimal amountPaid,
                   String paymentDate, String paymentMethod) {
        this(paymentId, billId, amountPaid, paymentDate,
                paymentMethod == null ? null : PaymentMethod.valueOf(paymentMethod.toUpperCase()));
    }

    public int getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(int paymentId) {
        this.paymentId = paymentId;
    }

    public int getBillId() {
        return billId;
    }

    public void setBillId(int billId) {
        this.billId = billId;
    }

    public BigDecimal getAmountPaid() {
        return amountPaid;
    }

    public void setAmountPaid(BigDecimal amountPaid) {
        this.amountPaid = amountPaid;
    }

    public String getPaymentDate() {
        return paymentDate;
    }

    public void setPaymentDate(String paymentDate) {
        this.paymentDate = paymentDate;
    }

    public PaymentMethod getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(PaymentMethod paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    
    public String toString() {
        return "Payment{" +
                "paymentId=" + paymentId +
                ", billId=" + billId +
                ", amountPaid=" + amountPaid +
                ", paymentDate='" + paymentDate + '\'' +
                ", paymentMethod='" + paymentMethod + '\'' +
                '}';
    }
}