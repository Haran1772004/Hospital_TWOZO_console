package com.hospital;

import com.hospital.localfunctions.PatientLC;
import com.hospital.localfunctions.DoctorLC;
import com.hospital.localfunctions.UserLC;
import com.hospital.impl.PatientLCImpl;
import com.hospital.impl.DoctorLCImpl;
import com.hospital.impl.BillLCImpl;
import com.hospital.impl.UserLCImpl;
import com.hospital.impl.AppointmentLCImpl;
import com.hospital.impl.MedicalRecordLCImpl;
import com.hospital.impl.PrescriptionLCImpl;
import com.hospital.model.Bill;
import com.hospital.model.Department;
import com.hospital.model.Appointment;
import com.hospital.model.Doctor;
import com.hospital.model.Patient;
import com.hospital.model.Payment;
import com.hospital.model.User;
import com.hospital.service.BillingService;
import com.hospital.service.RegistrationService;
import com.hospital.util.PasswordUtil;
import com.hospital.util.ValidationUtil;
import junit.framework.TestCase;
import java.math.BigDecimal;
import com.hospital.model.AppointmentStatus;
import com.hospital.model.BillStatus;
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
        UserLC userLC = new UserLCImpl();
        String username = "unique_" + System.nanoTime();
        userLC.addUser(new User(username, PasswordUtil.hashPassword("Strong1!"), "PATIENT", 1));
        try {
            userLC.addUser(new User(username, PasswordUtil.hashPassword("Strong1!"), "PATIENT", 2));
            fail("Duplicate username should be rejected");
        } catch (IllegalArgumentException expected) { }

        PatientLC patientLC = new PatientLCImpl();
        String suffix = Long.toHexString(System.nanoTime());
        Patient patient = new Patient(0, "Alex Smith", "1990-01-01", "MALE", "555" + suffix.substring(suffix.length() - 7),
                "alex" + suffix + "@example.com", "Address", "ACTIVE");
        patientLC.addPatient(patient);
        try {
            patientLC.addPatient(new Patient(0, "Another Person", "1991-01-01", "FEMALE", patient.getPhone(),
                    "other" + suffix + "@example.com", "Address", "ACTIVE"));
            fail("Duplicate patient phone should be rejected");
        } catch (IllegalArgumentException expected) { }
    }

    public void testPatientRegistrationLinksAccount() {
        String suffix = Long.toHexString(System.nanoTime());
        RegistrationService service = new RegistrationService();
        Patient patient = service.registerPatient("patient_" + suffix, "Strong1!", "Jane Smith",
                "1990-05-20", "FEMALE", "555010" + (System.currentTimeMillis() % 1000),
                "jane" + suffix + "@example.com", "Main Street");
        assertTrue(patient.getPatientId() > 0);
        User user = new UserLCImpl().getUserByUsername("patient_" + suffix);
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
        } catch (IllegalArgumentException expected) { }
    }

    public void testPositiveNumber() {
        assertTrue(ValidationUtil.isPositiveNumber(new BigDecimal("1.00")));
        assertFalse(ValidationUtil.isPositiveNumber(BigDecimal.ZERO));
        assertFalse(ValidationUtil.isPositiveNumber(new BigDecimal("-1")));
        assertTrue(ValidationUtil.isValidPaymentMethod("cash"));
        assertFalse(ValidationUtil.isValidPaymentMethod("bitcoin"));
    }

    public void testDoctorDuplicatePhoneRejected() {
        DoctorLCAssertions.addUniqueDoctor();
    }

    public void testBillingRejectsOverpaymentAndTransitionsStatus() {
        Patient patient = new Patient(0, "Bill Patient", "1980-01-01", "FEMALE", "5550199999",
                "bill" + System.nanoTime() + "@example.com", "Address", "ACTIVE");
        new PatientLCImpl().addPatient(patient);
        Doctor doctor = new Doctor(0, "Bill Doctor", "Medicine", "5550198888",
            "billdoctor" + System.nanoTime() + "@example.com",
            new Department(1, "Billing", "Care", "ACTIVE"), "ACTIVE");
        new DoctorLCImpl().addDoctor(doctor);
        Appointment appointment = new Appointment(0, patient, doctor, "2099-09-03", "2:00 PM", AppointmentStatus.SCHEDULED);
        new com.hospital.impl.AppointmentLCImpl().bookAppointment(appointment);
        Bill bill = new Bill(0, patient, new BigDecimal("100"), BigDecimal.ZERO, BigDecimal.ZERO,
                BigDecimal.ZERO, "2026-09-03", "UNPAID");
        bill.setAppointment(appointment);
        new BillLCImpl().generateBill(bill);
        BillingService billingService = new BillingService();
        billingService.makePayment(new Payment(0, bill.getBillId(), new BigDecimal("40"), "2026-09-03", "CASH"));
        assertEquals(BillStatus.PARTIAL, bill.getStatus());
        billingService.makePayment(new Payment(0, bill.getBillId(), new BigDecimal("70"), "2026-09-03", "CARD"));
        assertEquals(BillStatus.PARTIAL, bill.getStatus());
        billingService.makePayment(new Payment(0, bill.getBillId(), new BigDecimal("60"), "2026-09-03", "UPI"));
        assertEquals(BillStatus.PAID, bill.getStatus());
    }

    public void testMedicalRecordPrescriptionAndTodayValidation() {
        Patient patient = new Patient(0, "Record Patient", "1980-01-01", "FEMALE", "5550177777",
                "record" + System.nanoTime() + "@example.com", "Address", "ACTIVE");
        new PatientLCImpl().addPatient(patient);
        Doctor doctor = new Doctor(0, "Record Doctor", "Medicine", "5550176666",
                "recorddoctor" + System.nanoTime() + "@example.com",
                new Department(1, "Records", "Care", "ACTIVE"), "ACTIVE");
        new DoctorLCImpl().addDoctor(doctor);
        Appointment appointment = new Appointment(0, patient, doctor, "2099-09-03", "2:00 PM", AppointmentStatus.SCHEDULED);
        new AppointmentLCImpl().bookAppointment(appointment);

        MedicalRecordLCAssertions.assertInvalidRecordDate(appointment, patient, doctor);
        MedicalRecord record = new MedicalRecord(0, appointment, patient, doctor, "Diagnosis", "Notes", LocalDate.now().toString());
        new MedicalRecordLCImpl().createMedicalRecord(record);
        assertEquals(AppointmentStatus.FINISHED, appointment.getStatus());

        try {
            new PrescriptionLCImpl().addPrescription(new Prescription(0, record.getRecordId(), "", "once", "5 days"));
            fail("Blank medicine name should be rejected");
        } catch (IllegalArgumentException expected) { }
    }

    private static final class MedicalRecordLCAssertions {
        static void assertInvalidRecordDate(Appointment appointment, Patient patient, Doctor doctor) {
            try {
                new MedicalRecordLCImpl().createMedicalRecord(new MedicalRecord(0, appointment, patient, doctor,
                    "Diagnosis", "Notes", LocalDate.now().minusDays(1).toString()));
                fail("Previous record date should be rejected");
            } catch (IllegalArgumentException expected) { }
        }
    }

    private static final class DoctorLCAssertions {
        static void addUniqueDoctor() {
            DoctorLC LC = new DoctorLCImpl();
            String suffix = Long.toHexString(System.nanoTime());
            Doctor doctor = new Doctor(0, "Dr Alex", "Surgery", "5550188888",
                    "dr" + suffix + "@example.com", new Department(1, "Surgery", "Care", "ACTIVE"), "ACTIVE");
            LC.addDoctor(doctor);
            try {
                LC.addDoctor(new Doctor(0, "Dr Other", "Medicine", doctor.getPhone(),
                        "other" + suffix + "@example.com", doctor.getDepartment(), "ACTIVE"));
                fail("Duplicate doctor phone should be rejected");
            } catch (IllegalArgumentException expected) { }
        }
    }
}