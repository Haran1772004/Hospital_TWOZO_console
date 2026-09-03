package com.hospital.service;

import com.hospital.localfunctions.DepartmentLC;
import com.hospital.localfunctions.DoctorLC;
import com.hospital.localfunctions.PatientLC;
import com.hospital.impl.DepartmentLCImpl;
import com.hospital.impl.DoctorLCImpl;
import com.hospital.impl.PatientLCImpl;
import com.hospital.model.Department;
import com.hospital.model.Doctor;
import com.hospital.model.Patient;
import com.hospital.model.AccountStatus;
import com.hospital.util.TablePrinter;

import java.util.List;

public class AdminService {

    private final DoctorLC doctorLC = new DoctorLCImpl();
    private final PatientLC patientLC = new PatientLCImpl();
    private final DepartmentLC departmentLC = new DepartmentLCImpl();

    public void viewHospitalRecords() {

        System.out.println("========== HOSPITAL RECORDS ==========");

        System.out.println("\n--- Departments ---");
        List<Department> departments = departmentLC.getAllDepartments();
        TablePrinter.printDepartments(departments);

        System.out.println("\n--- Doctors ---");
        List<Doctor> doctors = doctorLC.getAllDoctors();
        
        TablePrinter.printDoctors(doctors);

        System.out.println("\n--- Patients ---");
        List<Patient> patients = patientLC.getAllPatients().stream()
            .filter(patient -> AccountStatus.ACTIVE == patient.getStatus()).toList();
       
        TablePrinter.printPatients(patients);

        System.out.println("\n=======================================");
    }
}
