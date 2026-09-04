
package com.hospital.service;

import com.hospital.impl.DoctorLFImpl;
import com.hospital.impl.PatientLFImpl;
import com.hospital.impl.UserLFImpl;
import com.hospital.exception.BusinessRuleViolationException;
import com.hospital.exception.DuplicateResourceException;
import com.hospital.localfunctions.DoctorLF;
import com.hospital.localfunctions.PatientLF;
import com.hospital.localfunctions.UserLF;
import com.hospital.model.AccountStatus;
import com.hospital.model.Department;
import com.hospital.model.Doctor;
import com.hospital.model.Gender;
import com.hospital.model.Patient;
import com.hospital.model.User;
import com.hospital.util.PasswordUtil;
import com.hospital.util.ValidationUtil;

public class RegistrationService {
    private final PatientLF patientLF;
    private final DoctorLF doctorLF;
    private final UserLF userLF;

    public RegistrationService() {
        this(new PatientLFImpl(), new DoctorLFImpl(), new UserLFImpl());
    }

    public RegistrationService(
            PatientLF patientLF,
            DoctorLF doctorLF,
            UserLF userLF) {

        this.patientLF = patientLF;
        this.doctorLF = doctorLF;
        this.userLF = userLF;
    }

    public Patient registerPatient(
            String username,
            String password,
            String name,
            String dob,
            String gender,
            String phone,
            String email,
            String address) {

        validateAccount(username, password);
        validatePerson(name, dob, phone, email, gender, address);
        ensureUsernameAvailable(username);

        if (!isPatientEmailAvailable(email)) {
            throw new DuplicateResourceException("Patient email already exists");
        }

        if (!isPatientPhoneAvailable(phone)) {
            throw new DuplicateResourceException("Patient phone already exists");
        }

        Patient patient = new Patient(
                0,
                name.trim(),
                dob,
                gender.trim(),
                phone.trim(),
                email.trim(),
                address.trim(),
                AccountStatus.PENDING);

        patientLF.addPatient(patient);

        userLF.addUser(new User(
                username.trim(),
                PasswordUtil.hashPassword(password),
                "PATIENT",
                patient.getPatientId(),
                AccountStatus.PENDING));

        return patient;
    }

    public Doctor registerDoctor(
            String username,
            String password,
            String name,
            String specialization,
            String phone,
            String email,
            Department department) {

        validateAccount(username, password);
        validateDoctor(name, phone, email, specialization);

        if (department == null || AccountStatus.ACTIVE != department.getStatus()) {
            throw new BusinessRuleViolationException(
                    "Doctor must belong to an active department");
        }

        if (!ValidationUtil.isValidSpecialization(specialization)) {
            throw new IllegalArgumentException("Invalid specialization");
        }

        ensureUsernameAvailable(username);

        if (!isDoctorEmailAvailable(email)) {
            throw new DuplicateResourceException("Doctor email already exists");
        }

        if (!isDoctorPhoneAvailable(phone)) {
            throw new DuplicateResourceException("Doctor phone already exists");
        }

        Doctor doctor = new Doctor(
                0,
                name.trim(),
                specialization.trim(),
                phone.trim(),
                email.trim(),
                department,
                AccountStatus.PENDING);

        doctorLF.addDoctor(doctor);

        userLF.addUser(new User(
                username.trim(),
                PasswordUtil.hashPassword(password),
                "DOCTOR",
                doctor.getDoctorId(),
                AccountStatus.PENDING));

        return doctor;
    }

    private void validateAccount(String username, String password) {
        if (!ValidationUtil.isValidUsername(username)) {
            throw new IllegalArgumentException("Invalid username");
        }

        if (!ValidationUtil.isStrongPassword(password)) {
            throw new IllegalArgumentException(
                    "Password must contain letters, numbers, and a symbol");
        }
    }

    private void validatePerson(
            String name,
            String dob,
            String phone,
            String email,
            String gender,
            String address) {

        if (!ValidationUtil.isValidName(name)) {
            throw new IllegalArgumentException(
                    "Invalid name: must be 2-60 characters");
        }

        if (dob != null && !ValidationUtil.isValidDate(dob)) {
            throw new IllegalArgumentException(
                    "Invalid date of birth: use yyyy-MM-dd");
        }

        if (!ValidationUtil.isValidPhone(phone)) {
            throw new IllegalArgumentException(
                    "Invalid phone: use 7-15 permitted characters");
        }

        if (!ValidationUtil.isValidEmail(email)) {
            throw new IllegalArgumentException("Invalid email format");
        }

        if (!ValidationUtil.isValidAddress(address)) {
            throw new IllegalArgumentException(
                    "Invalid address: enter 5-100 characters");
        }

        try {
            Gender.valueOf(gender.trim().toUpperCase());
        } catch (RuntimeException exception) {
            throw new IllegalArgumentException(
                    "Invalid gender: choose MALE, FEMALE, or OTHER");
        }
    }

    private void validateDoctor(
            String name,
            String phone,
            String email,
            String specialization) {

        if (!ValidationUtil.isValidName(name)) {
            throw new IllegalArgumentException(
                    "Invalid name: must be 2-60 characters");
        }

        if (!ValidationUtil.isValidPhone(phone)) {
            throw new IllegalArgumentException(
                    "Invalid phone: use 7-15 permitted characters");
        }

        if (!ValidationUtil.isValidEmail(email)) {
            throw new IllegalArgumentException("Invalid email format");
        }

        if (!ValidationUtil.isValidSpecialization(specialization)) {
            throw new IllegalArgumentException("Invalid specialization");
        }
    }

    private void ensureUsernameAvailable(String username) {
        if (userLF.existsByUsername(username)) {
            throw new DuplicateResourceException("Username already exists");
        }
    }

    public boolean isUsernameAvailable(String username) {
        return !userLF.existsByUsername(username);
    }

    public boolean isPatientEmailAvailable(String email) {
        return patientLF.getAllPatients()
                .stream()
                .noneMatch(patient ->
                        patient.getEmail()
                                .trim()
                                .equalsIgnoreCase(email.trim()));
    }

    public boolean isPatientPhoneAvailable(String phone) {
        String normalized = phone.replaceAll("[^0-9]", "");

        return patientLF.getAllPatients()
                .stream()
                .noneMatch(patient ->
                        patient.getPhone()
                                .replaceAll("[^0-9]", "")
                                .equals(normalized));
    }

    public boolean isDoctorEmailAvailable(String email) {
        return doctorLF.getAllDoctors()
                .stream()
                .noneMatch(doctor ->
                        doctor.getEmail()
                                .trim()
                                .equalsIgnoreCase(email.trim()));
    }

    public boolean isDoctorPhoneAvailable(String phone) {
        String normalized = phone.replaceAll("[^0-9]", "");

        return doctorLF.getAllDoctors()
                .stream()
                .noneMatch(doctor ->
                        doctor.getPhone()
                                .replaceAll("[^0-9]", "")
                                .equals(normalized));
    }
}

