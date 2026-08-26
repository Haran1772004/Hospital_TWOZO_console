package com.hospital.util;

import com.hospital.model.*;
import java.util.Collections;
import java.util.List;

public class TablePrinter {

    public static void printAppointments(List<Appointment> appointments) {
        if (appointments == null || appointments.isEmpty()) {
            System.out.println("No appointments found.");
            return;
        }

        String[] headers = {"ID", "Patient Name", "Doctor Name", "Date", "Time", "Status"};
        String[][] data = new String[appointments.size()][6];

        for (int i = 0; i < appointments.size(); i++) {
            Appointment a = appointments.get(i);
            data[i][0] = String.valueOf(a.getAppointmentId());
            data[i][1] = a.getPatient() != null ? a.getPatient().getName() : "N/A";
            data[i][2] = a.getDoctor() != null ? a.getDoctor().getName() : "N/A";
            data[i][3] = a.getAppointmentDate() != null ? a.getAppointmentDate() : "";
            data[i][4] = a.getAppointmentTime() != null ? a.getAppointmentTime() : "";
            data[i][5] = a.getStatus() != null ? a.getStatus() : "";
        }

        printTable(headers, data);
    }

    public static void printAppointment(Appointment appointment) {
        if (appointment != null) {
            printAppointments(Collections.singletonList(appointment));
        } else {
            System.out.println("No appointment found.");
        }
    }

    public static void printPatients(List<Patient> patients) {
        if (patients == null || patients.isEmpty()) {
            System.out.println("No patients found.");
            return;
        }

        String[] headers = {"ID", "Name", "DOB", "Gender", "Phone", "Email", "Address", "Status"};
        String[][] data = new String[patients.size()][8];

        for (int i = 0; i < patients.size(); i++) {
            Patient p = patients.get(i);
            data[i][0] = String.valueOf(p.getPatientId());
            data[i][1] = p.getName() != null ? p.getName() : "";
            data[i][2] = p.getDob() != null ? p.getDob() : "";
            data[i][3] = p.getGender() != null ? p.getGender() : "";
            data[i][4] = p.getPhone() != null ? p.getPhone() : "";
            data[i][5] = p.getEmail() != null ? p.getEmail() : "";
            data[i][6] = p.getAddress() != null ? p.getAddress() : "";
            data[i][7] = p.getStatus() != null ? p.getStatus() : "";
        }

        printTable(headers, data);
    }

    /**
     * Prints patients with Username and Password columns.
     * NOTE: For academic/demo purposes only — never show credentials in production.
     */
    public static void printPatientsWithCredentials(List<Patient> patients) {
        if (patients == null || patients.isEmpty()) {
            System.out.println("No patients found.");
            return;
        }

        String[] headers = {"ID", "Name", "DOB", "Gender", "Phone", "Email", "Address", "Status", "Username", "Password"};
        String[][] data = new String[patients.size()][10];

        for (int i = 0; i < patients.size(); i++) {
            Patient p = patients.get(i);
            data[i][0] = String.valueOf(p.getPatientId());
            data[i][1] = p.getName() != null ? p.getName() : "";
            data[i][2] = p.getDob() != null ? p.getDob() : "";
            data[i][3] = p.getGender() != null ? p.getGender() : "";
            data[i][4] = p.getPhone() != null ? p.getPhone() : "";
            data[i][5] = p.getEmail() != null ? p.getEmail() : "";
            data[i][6] = p.getAddress() != null ? p.getAddress() : "";
            data[i][7] = p.getStatus() != null ? p.getStatus() : "";

            // Look up login credentials by matching linkedId == patientId and role == PATIENT
            User user = UserStore.getAllUsers().stream()
                    .filter(u -> "PATIENT".equals(u.getRole()) && u.getLinkedId() == p.getPatientId())
                    .findFirst().orElse(null);
            data[i][8] = user != null ? user.getUsername() : "N/A";
            data[i][9] = user != null ? user.getPassword() : "N/A";
        }

        printTable(headers, data);
    }

    public static void printPatient(Patient patient) {
        if (patient != null) {
            printPatients(Collections.singletonList(patient));
        } else {
            System.out.println("No patient found.");
        }
    }

    /**
     * Prints a single patient's details with an extra "Login ID" column
     * showing their patientId (which equals their UserStore linkedId).
     * Used by DoctorMenu so the doctor can see the patient's system login reference.
     */
    public static void printPatientWithLinkedId(Patient patient) {
        if (patient == null) {
            System.out.println("No patient found.");
            return;
        }
        String[] headers = {"ID", "Name", "DOB", "Gender", "Phone", "Email", "Address", "Status", "Login ID"};
        String[][] data = new String[1][9];
        data[0][0] = String.valueOf(patient.getPatientId());
        data[0][1] = patient.getName()    != null ? patient.getName()    : "";
        data[0][2] = patient.getDob()     != null ? patient.getDob()     : "";
        data[0][3] = patient.getGender()  != null ? patient.getGender()  : "";
        data[0][4] = patient.getPhone()   != null ? patient.getPhone()   : "";
        data[0][5] = patient.getEmail()   != null ? patient.getEmail()   : "";
        data[0][6] = patient.getAddress() != null ? patient.getAddress() : "";
        data[0][7] = patient.getStatus()  != null ? patient.getStatus()  : "";
        // linkedId == patientId because UserStore.addUser sets linkedId = patient.getPatientId()
        data[0][8] = String.valueOf(patient.getPatientId());
        printTable(headers, data);
    }

    public static void printDoctors(List<Doctor> doctors) {
        if (doctors == null || doctors.isEmpty()) {
            System.out.println("No doctors found.");
            return;
        }

        String[] headers = {"ID", "Doctor Name", "Specialization", "Department", "Phone", "Email", "Status"};
        String[][] data = new String[doctors.size()][7];

        for (int i = 0; i < doctors.size(); i++) {
            Doctor d = doctors.get(i);
            data[i][0] = String.valueOf(d.getDoctorId());
            data[i][1] = d.getName() != null ? d.getName() : "";
            data[i][2] = d.getSpecialization() != null ? d.getSpecialization() : "";
            data[i][3] = d.getDepartment() != null ? d.getDepartment().getName() : "N/A";
            data[i][4] = d.getPhone() != null ? d.getPhone() : "";
            data[i][5] = d.getEmail() != null ? d.getEmail() : "";
            data[i][6] = d.getStatus() != null ? d.getStatus() : "";
        }

        printTable(headers, data);
    }

    /**
     * Prints doctors with Username and Password columns.
     * NOTE: For academic/demo purposes only — never show credentials in production.
     */
    public static void printDoctorsWithCredentials(List<Doctor> doctors) {
        if (doctors == null || doctors.isEmpty()) {
            System.out.println("No doctors found.");
            return;
        }

        String[] headers = {"ID", "Doctor Name", "Specialization", "Department", "Phone", "Email", "Status", "Username", "Password"};
        String[][] data = new String[doctors.size()][9];

        for (int i = 0; i < doctors.size(); i++) {
            Doctor d = doctors.get(i);
            data[i][0] = String.valueOf(d.getDoctorId());
            data[i][1] = d.getName() != null ? d.getName() : "";
            data[i][2] = d.getSpecialization() != null ? d.getSpecialization() : "";
            data[i][3] = d.getDepartment() != null ? d.getDepartment().getName() : "N/A";
            data[i][4] = d.getPhone() != null ? d.getPhone() : "";
            data[i][5] = d.getEmail() != null ? d.getEmail() : "";
            data[i][6] = d.getStatus() != null ? d.getStatus() : "";

            // Look up login credentials by matching linkedId == doctorId and role == DOCTOR
            User user = UserStore.getAllUsers().stream()
                    .filter(u -> "DOCTOR".equals(u.getRole()) && u.getLinkedId() == d.getDoctorId())
                    .findFirst().orElse(null);
            data[i][7] = user != null ? user.getUsername() : "N/A";
            data[i][8] = user != null ? user.getPassword() : "N/A";
        }

        printTable(headers, data);
    }

    public static void printDoctor(Doctor doctor) {
        if (doctor != null) {
            printDoctors(Collections.singletonList(doctor));
        } else {
            System.out.println("No doctor found.");
        }
    }

    public static void printDepartments(List<Department> departments) {
        if (departments == null || departments.isEmpty()) {
            System.out.println("No departments found.");
            return;
        }

        String[] headers = {"ID", "Department Name", "Description", "Status"};
        String[][] data = new String[departments.size()][4];

        for (int i = 0; i < departments.size(); i++) {
            Department dept = departments.get(i);
            data[i][0] = String.valueOf(dept.getDepartmentId());
            data[i][1] = dept.getName() != null ? dept.getName() : "";
            data[i][2] = dept.getDescription() != null ? dept.getDescription() : "";
            data[i][3] = dept.getStatus() != null ? dept.getStatus() : "";
        }

        printTable(headers, data);
    }

    public static void printDepartment(Department department) {
        if (department != null) {
            printDepartments(Collections.singletonList(department));
        } else {
            System.out.println("No department found.");
        }
    }

    public static void printMedicalRecords(List<MedicalRecord> records) {
        if (records == null || records.isEmpty()) {
            System.out.println("No medical records found.");
            return;
        }

        String[] headers = {"Record ID", "Appt ID", "Patient", "Doctor", "Diagnosis", "Treatment Notes", "Date"};
        String[][] data = new String[records.size()][7];

        for (int i = 0; i < records.size(); i++) {
            MedicalRecord r = records.get(i);
            data[i][0] = String.valueOf(r.getRecordId());
            data[i][1] = r.getAppointment() != null ? String.valueOf(r.getAppointment().getAppointmentId()) : "N/A";
            data[i][2] = r.getPatient() != null ? r.getPatient().getName() : "N/A";
            data[i][3] = r.getDoctor() != null ? r.getDoctor().getName() : "N/A";
            data[i][4] = r.getDiagnosis() != null ? r.getDiagnosis() : "";
            data[i][5] = r.getTreatmentNotes() != null ? r.getTreatmentNotes() : "";
            data[i][6] = r.getRecordDate() != null ? r.getRecordDate() : "";
        }

        printTable(headers, data);
    }

    public static void printMedicalRecord(MedicalRecord record) {
        if (record != null) {
            printMedicalRecords(Collections.singletonList(record));
        } else {
            System.out.println("No medical record found.");
        }
    }

    public static void printPrescriptions(List<Prescription> prescriptions) {
        if (prescriptions == null || prescriptions.isEmpty()) {
            System.out.println("No prescriptions found.");
            return;
        }

        String[] headers = {"Presc ID", "Record ID", "Medicine Name", "Dosage", "Duration"};
        String[][] data = new String[prescriptions.size()][5];

        for (int i = 0; i < prescriptions.size(); i++) {
            Prescription p = prescriptions.get(i);
            data[i][0] = String.valueOf(p.getPrescriptionId());
            data[i][1] = String.valueOf(p.getRecordId());
            data[i][2] = p.getMedicineName() != null ? p.getMedicineName() : "";
            data[i][3] = p.getDosage() != null ? p.getDosage() : "";
            data[i][4] = p.getDuration() != null ? p.getDuration() : "";
        }

        printTable(headers, data);
    }

    public static void printPrescription(Prescription prescription) {
        if (prescription != null) {
            printPrescriptions(Collections.singletonList(prescription));
        } else {
            System.out.println("No prescription found.");
        }
    }

    public static void printBills(List<Bill> bills) {
        if (bills == null || bills.isEmpty()) {
            System.out.println("No bills found.");
            return;
        }

        String[] headers = {"Bill ID", "Patient Name", "Consultation", "Medicine", "Other", "Total", "Date", "Status"};
        String[][] data = new String[bills.size()][8];

        for (int i = 0; i < bills.size(); i++) {
            Bill b = bills.get(i);
            data[i][0] = String.valueOf(b.getBillId());
            data[i][1] = b.getPatient() != null ? b.getPatient().getName() : "N/A";
            data[i][2] = b.getConsultationCharge() != null ? b.getConsultationCharge().toString() : "0.00";
            data[i][3] = b.getMedicineCharge() != null ? b.getMedicineCharge().toString() : "0.00";
            data[i][4] = b.getOtherCharge() != null ? b.getOtherCharge().toString() : "0.00";
            data[i][5] = b.getTotalAmount() != null ? b.getTotalAmount().toString() : "0.00";
            data[i][6] = b.getBillDate() != null ? b.getBillDate() : "";
            data[i][7] = b.getStatus() != null ? b.getStatus() : "";
        }

        printTable(headers, data);
    }

    public static void printBill(Bill bill) {
        if (bill != null) {
            printBills(Collections.singletonList(bill));
        } else {
            System.out.println("No bill found.");
        }
    }

    public static void printPayments(List<Payment> payments) {
        if (payments == null || payments.isEmpty()) {
            System.out.println("No payments found.");
            return;
        }

        String[] headers = {"Payment ID", "Bill ID", "Amount Paid", "Payment Date", "Payment Method"};
        String[][] data = new String[payments.size()][5];

        for (int i = 0; i < payments.size(); i++) {
            Payment p = payments.get(i);
            data[i][0] = String.valueOf(p.getPaymentId());
            data[i][1] = String.valueOf(p.getBillId());
            data[i][2] = p.getAmountPaid() != null ? p.getAmountPaid().toString() : "0.00";
            data[i][3] = p.getPaymentDate() != null ? p.getPaymentDate() : "";
            data[i][4] = p.getPaymentMethod() != null ? p.getPaymentMethod() : "";
        }

        printTable(headers, data);
    }

    public static void printPayment(Payment payment) {
        if (payment != null) {
            printPayments(Collections.singletonList(payment));
        } else {
            System.out.println("No payment found.");
        }
    }

    // Maximum characters allowed per cell before truncating with "..."
    // Keeps every table within a readable terminal width.
    private static final int MAX_COL_WIDTH = 20;

    /** Truncates a string to MAX_COL_WIDTH, appending "..." if cut. */
    private static String truncate(String value) {
        if (value == null) return "";
        if (value.length() <= MAX_COL_WIDTH) return value;
        return value.substring(0, MAX_COL_WIDTH - 3) + "...";
    }

    private static void printTable(String[] headers, String[][] data) {
        int columns = headers.length;
        int[] columnWidths = new int[columns];

        // Column width starts at the header length (capped at MAX_COL_WIDTH)
        for (int i = 0; i < columns; i++) {
            columnWidths[i] = Math.min(headers[i].length(), MAX_COL_WIDTH);
        }

        // Expand to fit data values, but never exceed MAX_COL_WIDTH
        for (String[] row : data) {
            for (int i = 0; i < columns; i++) {
                int cellLen = (row[i] != null) ? Math.min(row[i].length(), MAX_COL_WIDTH) : 0;
                if (cellLen > columnWidths[i]) {
                    columnWidths[i] = cellLen;
                }
            }
        }

        StringBuilder lineBuilder = new StringBuilder();
        lineBuilder.append("+");
        for (int width : columnWidths) {
            lineBuilder.append("-".repeat(width + 2)).append("+");
        }
        String separatorLine = lineBuilder.toString();

        System.out.println(separatorLine);

        // Print headers (truncated if needed)
        System.out.print("|");
        for (int i = 0; i < columns; i++) {
            System.out.printf(" %-" + columnWidths[i] + "s |", truncate(headers[i]));
        }
        System.out.println();

        System.out.println(separatorLine);

        // Print data rows (cells truncated if too long)
        for (String[] row : data) {
            System.out.print("|");
            for (int i = 0; i < columns; i++) {
                System.out.printf(" %-" + columnWidths[i] + "s |", truncate(row[i]));
            }
            System.out.println();
        }

        System.out.println(separatorLine);
    }
}
