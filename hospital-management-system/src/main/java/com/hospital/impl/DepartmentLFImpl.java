package com.hospital.impl;

import com.hospital.localfunctions.DepartmentLF;
import com.hospital.model.AccountStatus;
import com.hospital.model.Department;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class DepartmentLFImpl implements DepartmentLF {
  private static final List<Department> departments = new ArrayList<>();
  private static final AtomicInteger nextId = new AtomicInteger(1);

  @Override
  public void addDepartment(Department d) {
    if (d.takeDepartmentId() == 0) {
      d.setDepartmentId(nextId.getAndIncrement());
    }
    departments.add(d);
    System.out.println("Department added successfully (ID: " + d.takeDepartmentId() + ")");
  }

  @Override
  public void updateDepartment(Department d) {
    if (takeDepartmentById(d.takeDepartmentId()) == null) {
      System.out.println("Department not found with ID: " + d.takeDepartmentId());
    } else {
      System.out.println("Department updated successfully");
    }
  }

  @Override
  public void deactivateDepartment(int id) {
    Department d = takeDepartmentById(id);
    if (d == null) {
      System.out.println("Department not found with ID: " + id);
    } else {
      d.setStatus(AccountStatus.INACTIVE);
      System.out.println("Department deactivated successfully");
    }
  }

  @Override
  public void activateDepartment(int id) {
    Department d = takeDepartmentById(id);
    if (d == null) {
      System.out.println("Department not found with ID: " + id);
    } else {
      d.setStatus(AccountStatus.ACTIVE);
      System.out.println("Department activated successfully");
    }
  }

  @Override
  public Department takeDepartmentById(int id) {
    return departments.stream().filter(d -> d.takeDepartmentId() == id).findFirst().orElse(null);
  }

  @Override
  public List<Department> takeAllDepartments() {
    return new ArrayList<>(departments);
  }
}
