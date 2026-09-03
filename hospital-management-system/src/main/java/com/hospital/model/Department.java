package com.hospital.model;

public class Department {

    private int departmentId;
    private String name;
    private String description;
    private AccountStatus status;

    public Department() {
    }

    public Department(int departmentId, String name, String description, AccountStatus status) {
      
        this.departmentId = departmentId;
        this.name = name;
        this.description = description;
        this.status = status;
    }

    public Department(int departmentId, String name, String description, String status) {
        this(departmentId, name, description,
                status == null ? null : AccountStatus.valueOf(status.toUpperCase()));
    }

    public int getDepartmentId() {
        return departmentId;
    }

    public void setDepartmentId(int departmentId) {
        this.departmentId = departmentId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public AccountStatus getStatus() {
        return status;
    }

    public void setStatus(AccountStatus status) {
        this.status = status;
    }

    public void setStatus(String status) { 
        this.status = AccountStatus.valueOf(status.toUpperCase()); 
    }

    
    public String toString() {
        return "Department{" +
                "departmentId=" + departmentId +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", status='" + status + '\'' +
                '}';
    }
}