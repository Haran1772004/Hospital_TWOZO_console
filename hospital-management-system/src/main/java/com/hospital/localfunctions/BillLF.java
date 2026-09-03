package com.hospital.localfunctions;

import com.hospital.model.Bill;
import java.util.List;

public interface BillLF {

    void generateBill(Bill bill);

    void updateBillStatus(int billId, String status);

    Bill getBillById(int billId);

    List<Bill> getBillsByPatient(int patientId);

    List<Bill> getAllBills();

    boolean hasBillForAppointment(int appointmentId);
}