package com.hospital;

import com.hospital.exception.BusinessRuleViolationException;
import com.hospital.exception.DuplicateResourceException;
import com.hospital.localfunctions.PatientLF;
import com.hospital.localfunctions.DoctorLF;
import com.hospital.localfunctions.UserLF;
import com.hospital.impl.PatientLFImpl;
import com.hospital.impl.DoctorLFImpl;
import com.hospital.impl.UserLFImpl;
import com.hospital.impl.AppointmentLFImpl;
import com.hospital.impl.MedicalRecordLFImpl;
import com.hospital.impl.PrescriptionLFImpl;
import com.hospital.model.Department;
import com.hospital.model.Appointment;
import com.hospital.model.Doctor;
import com.hospital.model.Patient;
import com.hospital.model.User;
import com.hospital.service.RegistrationService;
import com.hospital.util.PasswordUtil;
import com.hospital.util.ValidationUtil;
import junit.framework.TestCase;
import com.hospital.model.AppointmentStatus;
import com.hospital.model.MedicalRecord;
import com.hospital.model.Prescription;
import java.time.LocalDate;

public class SecurityAndValidationTest extends TestCase {
    public void testBcryptAndStrictValidation() {
        String hash = PasswordUtil.hashPassword("Strong1!");
        assertFalse(hash.equals("Strong1!"));
        assertTrue(PasswordUtil.matches("Strong1!", hash));
        assertFalse(PasswordUtil.matches("Wrong1!", hash));
        assertTrue(ValidationUtil.isValidUsername("user_name1"));
        assertFalse(ValidationUtil.isValidUsername("bad-name"));
        assertTrue(ValidationUtil.isStrongPassword("Strong1!"));
        assertFalse(ValidationUtil.isStrongPassword("weakpass"));
        assertFalse(ValidationUtil.isValidDate("2026-02-30"));
        assertFalse(ValidationUtil.isValidTime("25:80"));
        assertTrue(ValidationUtil.isValidDate("2026-02-28"));
        assertTrue(ValidationUtil.isValidTime("9:30 AM"));
    }

    public void testDuplicateUsernameAndPatientPhoneRejected() {
        UserLF userLF = new UserLFImpl();
        String username = "unique_" + System.nanoTime();
        userLF.addUser(new User(username, PasswordUtil.hashPassword("Strong1!"), "PATIENT", 1));
        try {
            userLF.addUser(new User(username, PasswordUtil.hashPassword("Strong1!"), "PATIENT", 2));
            fail("Duplicate username should be rejected");
        } catch (DuplicateResourceException expected) {
        }

        PatientLF patientLF = new PatientLFImpl();
        String suffix = Long.toHexString(System.nanoTime());
        Patient patient = new Patient(0, "Alex Smith", "1990-01-01", "MALE", "555" + suffix.substring(suffix.length() - 7),
                "alex" + suffix + "@example.com", "Address", "ACTIVE");
        patientLF.addPatient(patient);
        try {
            patientLF.addPatient(new Patient(0, "Another Person", "1991-01-01", "FEMALE", patient.getPhone(),
                    "other" + suffix + "@example.com", "Address", "ACTIVE"));
            fail("Duplicate patient phone should be rejected");
        } catch (DuplicateResourceException expected) {
        }
    }

    public void testPatientRegistrationLinksAccount() {
        String suffix = Long.toHexString(System.nanoTime());
        RegistrationService service = new RegistrationService();
        Patient patient = service.registerPatient("patient_" + suffix, "Strong1!", "Jane Smith",
                "1990-05-20", "FEMALE", "555010" + (System.currentTimeMillis() % 1000),
                "jane" + suffix + "@example.com", "Main Street");
        assertTrue(patient.getPatientId() > 0);
        User user = new UserLFImpl().getUserByUsername("patient_" + suffix);
        assertNotNull(user);
        assertEquals(patient.getPatientId(), user.getLinkedId());
        assertTrue(PasswordUtil.matches("Strong1!", user.getPassword()));
    }

    public void testInactiveDepartmentRejectsDoctorRegistration() {
        String suffix = Long.toHexString(System.nanoTime());
        try {
            new RegistrationService().registerDoctor("doctor_" + suffix, "Strong1!", "Dr Jane",
                    "Cardiology", "5550109999", "doctor" + suffix + "@example.com",
                    new Department(1, "Cardiology", "Heart care", "INACTIVE"));
            fail("Inactive department should be rejected");
        } catch (BusinessRuleViolationException expected) {
        }
    }

    public void testDoctorDuplicatePhoneRejected() {
        DoctorLFAssertions.addUniqueDoctor();
    }

    public void testMedicalRecordPrescriptionAndTodayValidation() {
        Patient patient = new Patient(0, "Record Patient", "1980-01-01", "FEMALE", "5550177777",
                "record" + System.nanoTime() + "@example.com", "Address", "ACTIVE");
        new PatientLFImpl().addPatient(patient);
        Doctor doctor = new Doctor(0, "Record Doctor", "Medicine", "5550176666",
                "recorddoctor" + System.nanoTime() + "@example.com",
                new Department(1, "Records", "Care", "ACTIVE"), "ACTIVE");
        new DoctorLFImpl().addDoctor(doctor);
        Appointment appointment = new Appointment(0, patient, doctor, "2099-09-03", "2:00 PM", AppointmentStatus.SCHEDULED);
        new AppointmentLFImpl().bookAppointment(appointment);

        MedicalRecordLFAssertions.assertInvalidRecordDate(appointment, patient, doctor);
        MedicalRecord record = new MedicalRecord(0, appointment, patient, doctor, "Diagnosis", "Notes", LocalDate.now().toString());
        new MedicalRecordLFImpl().createMedicalRecord(record);
        assertEquals(AppointmentStatus.FINISHED, appointment.getStatus());

        try {
            new PrescriptionLFImpl().addPrescription(new Prescription(0, record.getRecordId(), "", "once", "5 days"));
            fail("Blank medicine name should be rejected");
        } catch (IllegalArgumentException expected) {
        }
    }

    private static final class MedicalRecordLFAssertions {
        static void assertInvalidRecordDate(Appointment appointment, Patient patient, Doctor doctor) {
            try {
                new MedicalRecordLFImpl().createMedicalRecord(new MedicalRecord(0, appointment, patient, doctor,
                    "Diagnosis", "Notes", LocalDate.now().minusDays(1).toString()));
                fail("Previous record date should be rejected");
            } catch (BusinessRuleViolationException expected) {
            }
        }
    }

    private static final class DoctorLFAssertions {
        static void addUniqueDoctor() {
            DoctorLF LF = new DoctorLFImpl();
            String suffix = Long.toHexString(System.nanoTime());
            Doctor doctor = new Doctor(0, "Dr Alex", "Surgery", "5550188888",
                    "dr" + suffix + "@example.com", new Department(1, "Surgery", "Care", "ACTIVE"), "ACTIVE");
            LF.addDoctor(doctor);
            try {
                LF.addDoctor(new Doctor(0, "Dr Other", "Medicine", doctor.getPhone(),
                        "other" + suffix + "@example.com", doctor.getDepartment(), "ACTIVE"));
                fail("Duplicate doctor phone should be rejected");
            } catch (DuplicateResourceException expected) {
            }
        }
    }
}