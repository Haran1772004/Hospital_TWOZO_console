package com.hospital.localfunctions;

import com.hospital.model.Department;
import java.util.List;

public interface DepartmentLF {

    void addDepartment(Department department);

    void updateDepartment(Department department);

    void deactivateDepartment(int departmentId);

    void activateDepartment(int departmentId);

    Department getDepartmentById(int departmentId);

    List<Department> getAllDepartments();
}
