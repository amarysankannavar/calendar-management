package com.example.CalendarManagement.DTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.UniqueElements;

import javax.persistence.Column;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class EmployeeDTO {
    private int employeeId;
    @NotBlank(message = "Employee name cannot be empty.")
    private String name;
    @NotBlank(message = "Work email cannot be empty.")
    @Email(message = "Invalid email format.")
    private String workEmail;
    private int officeId;
    private boolean isActive;



}
