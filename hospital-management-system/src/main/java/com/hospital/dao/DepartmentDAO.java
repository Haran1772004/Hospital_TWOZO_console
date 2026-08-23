package com.hospital.dao;

import com.hospital.model.Department;
import java.util.List;

public interface DepartmentDAO {

    void addDepartment(Department department);

    void updateDepartment(Department department);

    void deactivateDepartment(int departmentId);

    Department getDepartmentById(int departmentId);

    List<Department> getAllDepartments();
}
