package com.hospital.service;

import com.hospital.dao.DepartmentDAO;
import com.hospital.dao.DoctorDAO;
import com.hospital.dao.PatientDAO;
import com.hospital.impl.DepartmentDAOImpl;
import com.hospital.impl.DoctorDAOImpl;
import com.hospital.impl.PatientDAOImpl;
import com.hospital.model.Department;
import com.hospital.model.Doctor;
import com.hospital.model.Patient;
import com.hospital.model.User;
import com.hospital.util.TablePrinter;
import com.hospital.util.UserStore;

import java.util.List;

public class AdminService {

    private final DoctorDAO doctorDAO = new DoctorDAOImpl();
    private final PatientDAO patientDAO = new PatientDAOImpl();
    private final DepartmentDAO departmentDAO = new DepartmentDAOImpl();

    public void viewHospitalRecords() {

        System.out.println("========== HOSPITAL RECORDS ==========");

        System.out.println("\n--- Departments ---");
        List<Department> departments = departmentDAO.getAllDepartments();
        TablePrinter.printDepartments(departments);

        System.out.println("\n--- Doctors ---");
        List<Doctor> doctors = doctorDAO.getAllDoctors();
        // NOTE: Username/Password columns shown here for academic/demo purposes only.
        //       In a real production system these credentials would NEVER be displayed.
        TablePrinter.printDoctorsWithCredentials(doctors);

        System.out.println("\n--- Patients ---");
        List<Patient> patients = patientDAO.getAllPatients();
        // NOTE: Username/Password columns shown here for academic/demo purposes only.
        //       In a real production system these credentials would NEVER be displayed.
        TablePrinter.printPatientsWithCredentials(patients);

        System.out.println("\n=======================================");
    }
}
