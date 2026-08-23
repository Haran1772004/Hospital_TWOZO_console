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

import java.util.List;

public class AdminService {

    private final DoctorDAO doctorDAO = new DoctorDAOImpl();
    private final PatientDAO patientDAO = new PatientDAOImpl();
    private final DepartmentDAO departmentDAO = new DepartmentDAOImpl();

    // TODO: wire these in once AppointmentDAO and MedicalRecordDAO exist
    // private final AppointmentDAO appointmentDAO = new AppointmentDAOImpl();
    // private final MedicalRecordDAO medicalRecordDAO = new MedicalRecordDAOImpl();

    public void viewHospitalRecords() {

        System.out.println("========== HOSPITAL RECORDS ==========");

        System.out.println("\n--- Departments ---");
        List<Department> departments = departmentDAO.getAllDepartments();
        departments.forEach(System.out::println);

        System.out.println("\n--- Doctors ---");
        List<Doctor> doctors = doctorDAO.getAllDoctors();
        doctors.forEach(System.out::println);

        System.out.println("\n--- Patients ---");
        List<Patient> patients = patientDAO.getAllPatients();
        patients.forEach(System.out::println);

        System.out.println("\n--- Appointments ---");
        System.out.println("(Not available yet — AppointmentDAO not implemented)");

        System.out.println("\n--- Medical Records ---");
        System.out.println("(Not available yet — MedicalRecordDAO not implemented)");

        System.out.println("\n=======================================");
    }
}