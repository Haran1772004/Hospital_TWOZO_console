package com.hospital.impl;

import com.hospital.localfunctions.BillLC;
import com.hospital.model.Bill;
import com.hospital.util.ValidationUtil;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;

public class BillLCImpl implements BillLC {
    private static final List<Bill> bills = new CopyOnWriteArrayList<>();
    private static final AtomicInteger nextId = new AtomicInteger(1);
    private static final Comparator<Bill> newest = Comparator.comparing(Bill::getBillDate, Comparator.nullsLast(String::compareTo)).reversed();
    @Override public void generateBill(Bill b) {
        if (b == null || b.getAppointment() == null || b.getPatient() == null || !ValidationUtil.isValidDate(b.getBillDate())) throw new IllegalArgumentException("Appointment, patient, and valid bill date are required");
        if (!LocalDate.now().toString().equals(b.getBillDate())) throw new IllegalArgumentException("Bill date must be today");
        if (hasBillForAppointment(b.getAppointment().getAppointmentId())) throw new IllegalArgumentException("An appointment can have only one bill");
        if (!isNonNegative(b.getConsultationCharge()) || !isNonNegative(b.getMedicineCharge()) || !isNonNegative(b.getOtherCharge())) throw new IllegalArgumentException("Bill charges cannot be negative");
        if (b.getBillId() == 0) b.setBillId(nextId.getAndIncrement());
        BigDecimal total = b.getConsultationCharge().add(b.getMedicineCharge()).add(b.getOtherCharge());
        b.setTotalAmount(total);
        if (b.getStatus() == null) b.setStatus("UNPAID");
        bills.add(b);
        System.out.println("Bill generated successfully (ID: " + b.getBillId() + ")");
    }
    @Override public void updateBillStatus(int id, String status) { Bill b = getBillById(id); if (b != null) b.setStatus(status); }
    @Override public Bill getBillById(int id) { return bills.stream().filter(b -> b.getBillId() == id).findFirst().orElse(null); }
    @Override public List<Bill> getBillsByPatient(int id) { return bills.stream().filter(b -> b.getPatient().getPatientId() == id).sorted(newest).toList(); }
    @Override public List<Bill> getAllBills() { return bills.stream().sorted(newest).toList(); }
    @Override public boolean hasBillForAppointment(int appointmentId) { return bills.stream().anyMatch(b -> b.getAppointment() != null && b.getAppointment().getAppointmentId() == appointmentId); }
    private boolean isNonNegative(BigDecimal value) { return value != null && value.compareTo(BigDecimal.ZERO) >= 0; }
}
