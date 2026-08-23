package com.hospital.dao;

import com.hospital.model.Bill;
import java.util.List;

public interface BillDAO {

    void generateBill(Bill bill);

    void updateBillStatus(int billId, String status);

    Bill getBillById(int billId);

    List<Bill> getBillsByPatient(int patientId);

    List<Bill> getAllBills();
}