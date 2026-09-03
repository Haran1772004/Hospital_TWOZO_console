package com.hospital.impl;

import com.hospital.localfunctions.DepartmentLC;
import com.hospital.model.Department;
import com.hospital.model.AccountStatus;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class DepartmentLCImpl implements DepartmentLC {
    private static final List<Department> departments = new ArrayList<>();
    private static final AtomicInteger nextId = new AtomicInteger(1);

    @Override
    public void addDepartment(Department d) {
        if (d.getDepartmentId() == 0) d.setDepartmentId(nextId.getAndIncrement());
        departments.add(d);
        System.out.println("Department added successfully (ID: " + d.getDepartmentId() + ")");
    }

    @Override
    public void updateDepartment(Department d) {
        if (getDepartmentById(d.getDepartmentId()) == null)
            System.out.println("Department not found with ID: " + d.getDepartmentId());
        else
            System.out.println("Department updated successfully");
    }

    @Override
    public void deactivateDepartment(int id) {
        Department d = getDepartmentById(id);
        if (d == null)
            System.out.println("Department not found with ID: " + id);
        else {
            d.setStatus(AccountStatus.INACTIVE);
            System.out.println("Department deactivated successfully");
        }
    }

    @Override
    public void activateDepartment(int id) {
        Department d = getDepartmentById(id);
        if (d == null)
            System.out.println("Department not found with ID: " + id);
        else {
            d.setStatus(AccountStatus.ACTIVE);
            System.out.println("Department activated successfully");
        }
    }

    @Override
    public Department getDepartmentById(int id) {
        return departments.stream().filter(d -> d.getDepartmentId() == id).findFirst().orElse(null);
    }

    @Override
    public List<Department> getAllDepartments() {
        return new ArrayList<>(departments);
    }
}
