package com.hospital.service;

import com.hospital.impl.DepartmentLFImpl;
import com.hospital.impl.DoctorLFImpl;
import com.hospital.impl.PatientLFImpl;
import com.hospital.localfunctions.DepartmentLF;
import com.hospital.localfunctions.DoctorLF;
import com.hospital.localfunctions.PatientLF;
import com.hospital.model.AccountStatus;
import com.hospital.model.Department;
import com.hospital.model.Doctor;
import com.hospital.model.Patient;
import com.hospital.util.TablePrinter;
import java.util.List;

public class AdminService {

  private final DoctorLF doctorLF = new DoctorLFImpl();
  private final PatientLF patientLF = new PatientLFImpl();
  private final DepartmentLF departmentLF = new DepartmentLFImpl();

  public void viewHospitalRecords() {

    System.out.println("========== HOSPITAL RECORDS ==========");

    System.out.println("\n--- Departments ---");
    List<Department> departments = departmentLF.getAllDepartments();
    TablePrinter.printDepartments(departments);

    System.out.println("\n--- Doctors ---");
    List<Doctor> doctors = doctorLF.getAllDoctors();

    TablePrinter.printDoctors(doctors);

    System.out.println("\n--- Patients ---");
    List<Patient> patients =
        patientLF.getAllPatients().stream()
            .filter(patient -> AccountStatus.ACTIVE == patient.getStatus())
            .toList();

    TablePrinter.printPatients(patients);

    System.out.println("\n=======================================");
  }
}
