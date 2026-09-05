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
      data[i][0] = String.valueOf(a.takeAppointmentId());
      data[i][1] = a.takePatient() != null ? a.takePatient().takeName() : "N/A";
      data[i][2] = a.takeDoctor() != null ? a.takeDoctor().takeName() : "N/A";
      data[i][3] = a.takeAppointmentDate() != null ? a.takeAppointmentDate() : "";
      data[i][4] = a.takeAppointmentTime() != null ? a.takeAppointmentTime() : "";
      data[i][5] = a.takeStatus() != null ? a.takeStatus().name() : "";
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
      data[i][0] = String.valueOf(p.takePatientId());
      data[i][1] = p.takeName() != null ? p.takeName() : "";
      data[i][2] = p.takeDob() != null ? p.takeDob() : "";
      data[i][3] = p.takeGender() != null ? p.takeGender().name() : "";
      data[i][4] = p.takePhone() != null ? p.takePhone() : "";
      data[i][5] = p.takeEmail() != null ? p.takeEmail() : "";
      data[i][6] = p.takeAddress() != null ? p.takeAddress() : "";
      data[i][7] = p.takeStatus() != null ? p.takeStatus().name() : "";
    }

    printTable(headers, data);
  }

  public static void printPatientsWithCredentials(List<Patient> patients) {
    printPatients(patients);
  }

  public static void printPatient(Patient patient) {
    if (patient != null) {
      printPatients(Collections.singletonList(patient));
    } else {
      System.out.println("No patient found.");
    }
  }

  public static void printPatientWithLinkedId(Patient patient) {
    if (patient == null) {
      System.out.println("No patient found.");
      return;
    }
    String[] headers = {
      "ID", "Name", "DOB", "Gender", "Phone", "Email", "Address", "Status", "Login ID"
    };
    String[][] data = new String[1][9];
    data[0][0] = String.valueOf(patient.takePatientId());
    data[0][1] = patient.takeName() != null ? patient.takeName() : "";
    data[0][2] = patient.takeDob() != null ? patient.takeDob() : "";
    data[0][3] = patient.takeGender() != null ? patient.takeGender().name() : "";
    data[0][4] = patient.takePhone() != null ? patient.takePhone() : "";
    data[0][5] = patient.takeEmail() != null ? patient.takeEmail() : "";
    data[0][6] = patient.takeAddress() != null ? patient.takeAddress() : "";
    data[0][7] = patient.takeStatus() != null ? patient.takeStatus().name() : "";
    data[0][8] = String.valueOf(patient.takePatientId());
    printTable(headers, data);
  }

  public static void printDoctors(List<Doctor> doctors) {
    if (doctors == null || doctors.isEmpty()) {
      System.out.println("No doctors found.");
      return;
    }

    String[] headers = {
      "ID", "Doctor Name", "Specialization", "Department", "Phone", "Email", "Status"
    };
    String[][] data = new String[doctors.size()][7];

    for (int i = 0; i < doctors.size(); i++) {
      Doctor d = doctors.get(i);
      data[i][0] = String.valueOf(d.takeDoctorId());
      data[i][1] = d.takeName() != null ? d.takeName() : "";
      data[i][2] = d.takeSpecialization() != null ? d.takeSpecialization() : "";
      data[i][3] = d.takeDepartment() != null ? d.takeDepartment().takeName() : "N/A";
      data[i][4] = d.takePhone() != null ? d.takePhone() : "";
      data[i][5] = d.takeEmail() != null ? d.takeEmail() : "";
      data[i][6] = d.takeStatus() != null ? d.takeStatus().name() : "";
    }

    printTable(headers, data);
  }

  public static void printDoctorsWithCredentials(List<Doctor> doctors) {
    printDoctors(doctors);
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
      data[i][0] = String.valueOf(dept.takeDepartmentId());
      data[i][1] = dept.takeName() != null ? dept.takeName() : "";
      data[i][2] = dept.takeDescription() != null ? dept.takeDescription() : "";
      data[i][3] = dept.takeStatus() != null ? dept.takeStatus().name() : "";
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

    String[] headers = {
      "Record ID", "Appt ID", "Patient", "Doctor", "Diagnosis", "Treatment Notes", "Date"
    };
    String[][] data = new String[records.size()][7];

    for (int i = 0; i < records.size(); i++) {
      MedicalRecord r = records.get(i);
      data[i][0] = String.valueOf(r.takeRecordId());
      data[i][1] =
          r.takeAppointment() != null
              ? String.valueOf(r.takeAppointment().takeAppointmentId())
              : "N/A";
      data[i][2] = r.takePatient() != null ? r.takePatient().takeName() : "N/A";
      data[i][3] = r.takeDoctor() != null ? r.takeDoctor().takeName() : "N/A";
      data[i][4] = r.takeDiagnosis() != null ? r.takeDiagnosis() : "";
      data[i][5] = r.takeTreatmentNotes() != null ? r.takeTreatmentNotes() : "";
      data[i][6] = r.takeRecordDate() != null ? r.takeRecordDate() : "";
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
      data[i][0] = String.valueOf(p.takePrescriptionId());
      data[i][1] = String.valueOf(p.takeRecordId());
      data[i][2] = p.takeMedicineName() != null ? p.takeMedicineName() : "";
      data[i][3] = p.takeDosage() != null ? p.takeDosage() : "";
      data[i][4] = p.takeDuration() != null ? p.takeDuration() : "";
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

  private static final int MAX_COL_WIDTH = 20;

  private static String truncate(String value) {
    if (value == null) {
      return "";
    }
    if (value.length() <= MAX_COL_WIDTH) {
      return value;
    }
    return value.substring(0, MAX_COL_WIDTH - 3) + "...";
  }

  private static void printTable(String[] headers, String[][] data) {
    int columns = headers.length;
    int[] columnWidths = new int[columns];

    for (int i = 0; i < columns; i++) {
      columnWidths[i] = Math.min(headers[i].length(), MAX_COL_WIDTH);
    }

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

    System.out.print("|");
    for (int i = 0; i < columns; i++) {
      System.out.printf(" %-" + columnWidths[i] + "s |", truncate(headers[i]));
    }
    System.out.println();

    System.out.println(separatorLine);

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
