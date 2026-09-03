package com.hospital.service;

import com.hospital.localfunctions.DoctorLC;
import com.hospital.localfunctions.PatientLC;
import com.hospital.localfunctions.UserLC;
import com.hospital.model.Department;
import com.hospital.model.Doctor;
import com.hospital.model.Patient;
import com.hospital.model.User;
import com.hospital.model.AccountStatus;
import com.hospital.model.Gender;
import com.hospital.impl.DoctorLCImpl;
import com.hospital.impl.PatientLCImpl;
import com.hospital.impl.UserLCImpl;
import com.hospital.util.PasswordUtil;
import com.hospital.util.ValidationUtil;

public class RegistrationService {
    private final PatientLC patientLC;
    private final DoctorLC doctorLC;
    private final UserLC userLC;

    public RegistrationService() {
        this(new PatientLCImpl(), new DoctorLCImpl(), new UserLCImpl());
    }

    public RegistrationService(PatientLC patientLC, DoctorLC doctorLC, UserLC userLC) {
        this.patientLC = patientLC;
        this.doctorLC = doctorLC;
        this.userLC = userLC;
    }

    public Patient registerPatient(String username, String password, String name, String dob,
                                   String gender, String phone, String email, String address) {
        validateAccount(username, password);
        validatePerson(name, dob, phone, email, gender, address);
        ensureUsernameAvailable(username);
        if (!isPatientEmailAvailable(email)) throw new IllegalArgumentException("Patient email already exists");
        if (!isPatientPhoneAvailable(phone)) throw new IllegalArgumentException("Patient phone already exists");
        Patient patient = new Patient(0, name.trim(), dob, gender.trim(), phone.trim(),
            email.trim(), address.trim(), AccountStatus.PENDING);
        patientLC.addPatient(patient);
        userLC.addUser(new User(username.trim(), PasswordUtil.hashPassword(password), "PATIENT", patient.getPatientId(), AccountStatus.PENDING));
        return patient;
    }

    public Doctor registerDoctor(String username, String password, String name, String specialization,
                                 String phone, String email, Department department) {
        validateAccount(username, password);

        validateDoctor(name, phone, email, specialization);

        if (department == null || AccountStatus.ACTIVE != department.getStatus()) {
            throw new IllegalArgumentException("Doctor must belong to an active department");
        }

        if (!ValidationUtil.isValidSpecialization(specialization)) {
            throw new IllegalArgumentException("Invalid specialization");
        }
        
        ensureUsernameAvailable(username);
        if (!isDoctorEmailAvailable(email)) throw new IllegalArgumentException("Doctor email already exists");
        if (!isDoctorPhoneAvailable(phone)) throw new IllegalArgumentException("Doctor phone already exists");
        Doctor doctor = new Doctor(0, name.trim(), specialization.trim(), phone.trim(), email.trim(), department, AccountStatus.PENDING);
        doctorLC.addDoctor(doctor);
        userLC.addUser(new User(username.trim(), PasswordUtil.hashPassword(password), "DOCTOR", doctor.getDoctorId(), AccountStatus.PENDING));
        return doctor;
    }

    private void validateAccount(String username, String password) {
        if (!ValidationUtil.isValidUsername(username)) throw new IllegalArgumentException("Invalid username");
        if (!ValidationUtil.isStrongPassword(password)) {
            throw new IllegalArgumentException("Password must contain letters, numbers, and a symbol");
        }
    }

    private void validatePerson(String name, String dob, String phone, String email, String gender, String address) {
        if (!ValidationUtil.isValidName(name)) throw new IllegalArgumentException("Invalid name: must be 2-60 characters");
        if (dob != null && !ValidationUtil.isValidDate(dob)) throw new IllegalArgumentException("Invalid date of birth: use yyyy-MM-dd");
        if (!ValidationUtil.isValidPhone(phone)) throw new IllegalArgumentException("Invalid phone: use 7-15 permitted characters");
        if (!ValidationUtil.isValidEmail(email)) throw new IllegalArgumentException("Invalid email format");
        if (!ValidationUtil.isValidAddress(address)) throw new IllegalArgumentException("Invalid address: enter 5-100 characters");
        try { Gender.valueOf(gender.trim().toUpperCase()); }
        catch (RuntimeException exception) { throw new IllegalArgumentException("Invalid gender: choose MALE, FEMALE, or OTHER"); }
    }

    private void validateDoctor(String name, String phone, String email, String specialization) {
        if (!ValidationUtil.isValidName(name)) throw new IllegalArgumentException("Invalid name: must be 2-60 characters");
        if (!ValidationUtil.isValidPhone(phone)) throw new IllegalArgumentException("Invalid phone: use 7-15 permitted characters");
        if (!ValidationUtil.isValidEmail(email)) throw new IllegalArgumentException("Invalid email format");
        if (!ValidationUtil.isValidSpecialization(specialization)) throw new IllegalArgumentException("Invalid specialization");
    }

    private void ensureUsernameAvailable(String username) {
        if (userLC.existsByUsername(username)) throw new IllegalArgumentException("Username already exists");
    }

    public boolean isUsernameAvailable(String username) { return !userLC.existsByUsername(username); }

    public boolean isPatientEmailAvailable(String email) {
        return patientLC.getAllPatients().stream().noneMatch(patient -> patient.getEmail().trim().equalsIgnoreCase(email.trim()));
    }

    public boolean isPatientPhoneAvailable(String phone) {
        String normalized = phone.replaceAll("[^0-9]", "");
        return patientLC.getAllPatients().stream().noneMatch(patient -> patient.getPhone().replaceAll("[^0-9]", "").equals(normalized));
    }

    public boolean isDoctorEmailAvailable(String email) {
        return doctorLC.getAllDoctors().stream().noneMatch(doctor -> doctor.getEmail().trim().equalsIgnoreCase(email.trim()));
    }

    public boolean isDoctorPhoneAvailable(String phone) {
        String normalized = phone.replaceAll("[^0-9]", "");
        return doctorLC.getAllDoctors().stream().noneMatch(doctor -> doctor.getPhone().replaceAll("[^0-9]", "").equals(normalized));
    }
}
