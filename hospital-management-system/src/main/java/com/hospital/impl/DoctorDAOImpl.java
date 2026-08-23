package com.hospital.impl;

import com.hospital.dao.DoctorDAO;
import com.hospital.model.Department;
import com.hospital.model.Doctor;
import com.hospital.util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class DoctorDAOImpl implements DoctorDAO {

    @Override
    public void addDoctor(Doctor doctor) {

        String sql = """
                INSERT INTO doctor
                (name, specialization, phone, email, department_id, status)
                VALUES (?, ?, ?, ?, ?, ?)
                """;

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, doctor.getName());
            statement.setString(2, doctor.getSpecialization());
            statement.setString(3, doctor.getPhone());
            statement.setString(4, doctor.getEmail());

            if (doctor.getDepartment() == null) {
                throw new IllegalArgumentException("Department cannot be null");
            }

            statement.setInt(
                    5,
                    doctor.getDepartment().getDepartmentId()
            );

            statement.setString(6, doctor.getStatus());

            int rowsAffected = statement.executeUpdate();

            if (rowsAffected == 0) {
                throw new SQLException("Failed to add doctor");
            }

        } catch (SQLException e) {
            System.err.println("Error while adding doctor: " + e.getMessage());
            e.printStackTrace();
        }
    }


    @Override
    public void updateDoctor(Doctor doctor) {

        String sql = """
                UPDATE doctor
                SET name = ?,
                    specialization = ?,
                    phone = ?,
                    email = ?,
                    department_id = ?,
                    status = ?
                WHERE doctor_id = ?
                """;

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, doctor.getName());
            statement.setString(2, doctor.getSpecialization());
            statement.setString(3, doctor.getPhone());
            statement.setString(4, doctor.getEmail());

            if (doctor.getDepartment() == null) {
                throw new IllegalArgumentException("Department cannot be null");
            }

            statement.setInt(
                    5,
                    doctor.getDepartment().getDepartmentId()
            );

            statement.setString(6, doctor.getStatus());
            statement.setInt(7, doctor.getDoctorId());

            int rowsAffected = statement.executeUpdate();

            if (rowsAffected == 0) {
                System.out.println(
                        "No doctor found with ID: "
                                + doctor.getDoctorId()
                );
            }

        } catch (SQLException e) {
            System.err.println("Error while updating doctor: " + e.getMessage());
            e.printStackTrace();
        }
    }


    @Override
    public void deactivateDoctor(int doctorId) {

        String sql = """
                UPDATE doctor
                SET status = ?
                WHERE doctor_id = ?
                """;

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, "INACTIVE");
            statement.setInt(2, doctorId);

            int rowsAffected = statement.executeUpdate();

            if (rowsAffected == 0) {
                System.out.println(
                        "No doctor found with ID: " + doctorId
                );
            }

        } catch (SQLException e) {
            System.err.println(
                    "Error while deactivating doctor: " + e.getMessage()
            );
            e.printStackTrace();
        }
    }


    @Override
    public Doctor getDoctorById(int doctorId) {

        String sql = """
                SELECT
                    d.doctor_id,
                    d.name,
                    d.specialization,
                    d.phone,
                    d.email,
                    d.status,

                    dept.department_id,
                    dept.name AS department_name,
                    dept.description AS department_description,
                    dept.status AS department_status

                FROM doctor d

                JOIN department dept
                    ON d.department_id = dept.department_id

                WHERE d.doctor_id = ?
                """;

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setInt(1, doctorId);

            try (ResultSet resultSet = statement.executeQuery()) {

                if (resultSet.next()) {

                    Department department = new Department(
                            resultSet.getInt("department_id"),
                            resultSet.getString("department_name"),
                            resultSet.getString("department_description"),
                            resultSet.getString("department_status")
                    );

                    return new Doctor(
                            resultSet.getInt("doctor_id"),
                            resultSet.getString("name"),
                            resultSet.getString("specialization"),
                            resultSet.getString("phone"),
                            resultSet.getString("email"),
                            department,
                            resultSet.getString("status")
                    );
                }
            }

        } catch (SQLException e) {
            System.err.println(
                    "Error while getting doctor: " + e.getMessage()
            );
            e.printStackTrace();
        }

        return null;
    }


    @Override
    public List<Doctor> getAllDoctors() {

        List<Doctor> doctors = new ArrayList<>();

        String sql = """
                SELECT
                    d.doctor_id,
                    d.name,
                    d.specialization,
                    d.phone,
                    d.email,
                    d.status,

                    dept.department_id,
                    dept.name AS department_name,
                    dept.description AS department_description,
                    dept.status AS department_status

                FROM doctor d

                JOIN department dept
                    ON d.department_id = dept.department_id
                """;

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql);
             ResultSet resultSet =
                     statement.executeQuery()) {

            while (resultSet.next()) {

                Department department = new Department(
                        resultSet.getInt("department_id"),
                        resultSet.getString("department_name"),
                        resultSet.getString("department_description"),
                        resultSet.getString("department_status")
                );

                Doctor doctor = new Doctor(
                        resultSet.getInt("doctor_id"),
                        resultSet.getString("name"),
                        resultSet.getString("specialization"),
                        resultSet.getString("phone"),
                        resultSet.getString("email"),
                        department,
                        resultSet.getString("status")
                );

                doctors.add(doctor);
            }

        } catch (SQLException e) {
            System.err.println(
                    "Error while getting doctors: " + e.getMessage()
            );
            e.printStackTrace();
        }

        return doctors;
    }
}