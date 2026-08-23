package com.hospital.impl;

import com.hospital.dao.DepartmentDAO;
import com.hospital.model.Department;
import com.hospital.util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class DepartmentDAOImpl implements DepartmentDAO {

    // 1. ADD DEPARTMENT
    @Override
    public void addDepartment(Department department) {

        String sql = """
                INSERT INTO department
                (name, description, status)
                VALUES (?, ?, ?)
                """;

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setString(1, department.getName());
            statement.setString(2, department.getDescription());
            statement.setString(3, department.getStatus());

            int rowsAffected = statement.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("Department added successfully");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    // 2. UPDATE DEPARTMENT
    @Override
    public void updateDepartment(Department department) {

        String sql = """
                UPDATE department
                SET name = ?,
                    description = ?,
                    status = ?
                WHERE department_id = ?
                """;

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setString(1, department.getName());
            statement.setString(2, department.getDescription());
            statement.setString(3, department.getStatus());
            statement.setInt(4, department.getDepartmentId());

            int rowsAffected = statement.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("Department updated successfully");
            } else {
                System.out.println(
                        "Department not found with ID: "
                                + department.getDepartmentId()
                );
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    // 3. DEACTIVATE DEPARTMENT
    @Override
    public void deactivateDepartment(int departmentId) {

        String sql = """
                UPDATE department
                SET status = ?
                WHERE department_id = ?
                """;

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setString(1, "INACTIVE");
            statement.setInt(2, departmentId);

            int rowsAffected = statement.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("Department deactivated successfully");
            } else {
                System.out.println(
                        "Department not found with ID: " + departmentId
                );
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    // 4. GET DEPARTMENT BY ID
    @Override
    public Department getDepartmentById(int departmentId) {

        String sql = """
                SELECT department_id,
                       name,
                       description,
                       status
                FROM department
                WHERE department_id = ?
                """;

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            // Replace the first ? with departmentId
            statement.setInt(1, departmentId);

            try (ResultSet resultSet = statement.executeQuery()) {

                if (resultSet.next()) {

                    return new Department(
                            resultSet.getInt("department_id"),
                            resultSet.getString("name"),
                            resultSet.getString("description"),
                            resultSet.getString("status")
                    );
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }


    // 5. GET ALL DEPARTMENTS
    @Override
    public List<Department> getAllDepartments() {

        List<Department> departments = new ArrayList<>();

        String sql = """
                SELECT department_id,
                       name,
                       description,
                       status
                FROM department
                """;

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql);
             ResultSet resultSet =
                     statement.executeQuery()) {

            while (resultSet.next()) {

                Department department = new Department(
                        resultSet.getInt("department_id"),
                        resultSet.getString("name"),
                        resultSet.getString("description"),
                        resultSet.getString("status")
                );

                departments.add(department);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return departments;
    }
}