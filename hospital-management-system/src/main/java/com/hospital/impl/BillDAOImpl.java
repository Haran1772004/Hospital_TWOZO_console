package com.hospital.impl;

import com.hospital.dao.BillDAO;
import com.hospital.model.Bill;
import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;

public class BillDAOImpl implements BillDAO {
    private static final List<Bill> bills = new CopyOnWriteArrayList<>();
    private static final AtomicInteger nextId = new AtomicInteger(1);
    private static final Comparator<Bill> newest = Comparator.comparing(Bill::getBillDate, Comparator.nullsLast(String::compareTo)).reversed();
    @Override public void generateBill(Bill b) { if (b.getBillId() == 0) b.setBillId(nextId.getAndIncrement()); BigDecimal total = b.getConsultationCharge().add(b.getMedicineCharge()).add(b.getOtherCharge()); b.setTotalAmount(total); if (b.getStatus() == null) b.setStatus("UNPAID"); bills.add(b); System.out.println("Bill generated successfully (ID: " + b.getBillId() + ")"); }
    @Override public void updateBillStatus(int id, String status) { Bill b = getBillById(id); if (b != null) b.setStatus(status); }
    @Override public Bill getBillById(int id) { return bills.stream().filter(b -> b.getBillId() == id).findFirst().orElse(null); }
    @Override public List<Bill> getBillsByPatient(int id) { return bills.stream().filter(b -> b.getPatient().getPatientId() == id).sorted(newest).toList(); }
    @Override public List<Bill> getAllBills() { return bills.stream().sorted(newest).toList(); }
}
