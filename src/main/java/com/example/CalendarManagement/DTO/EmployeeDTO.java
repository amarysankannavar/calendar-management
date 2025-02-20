package com.example.CalendarManagement.DTO;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

public class EmployeeDTO {

    private boolean isActive;
    private int employeeId;

    @NotBlank(message = "Employee name cannot be empty.")
    private String name;

    @NotBlank(message = "Work email cannot be empty.")
    @Email(message = "Invalid email format.")
    private String workEmail;

    // Changed to reference OfficeModel instead of officeLocation string
    private int officeId;

    public EmployeeDTO(int employeeId, String name, String workEmail, int officeId, boolean isActive) {
        this.employeeId = employeeId;
        this.name = name;
        this.workEmail = workEmail;
        this.officeId = officeId;
        this.isActive = isActive;
    }

    public int getEmployeeId() {
        return employeeId;
    }

    public String getName() {
        return name;
    }

    public String getWorkEmail() {
        return workEmail;
    }

    public int getOfficeId() {
        return officeId;
    }

    public void setEmployeeId(int employeeId) {
        this.employeeId = employeeId;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setWorkEmail(String workEmail) {
        this.workEmail = workEmail;
    }

    public void setOfficeId(int officeId) {
        this.officeId = officeId;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        this.isActive = active;
    }
}
