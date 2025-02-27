package com.example.CalendarManagement.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;


@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

public class EmployeeModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @NotBlank(message = "Employee name cannot be empty okay.")
    private String name;

    @Column(unique = true, nullable = false)
    private String workEmail;

    @Column(columnDefinition = "BOOLEAN DEFAULT true")
    private boolean isActive=true;
    @ManyToOne
    @JoinColumn(name = "officeId", nullable = false)
    private OfficeModel office;



    public EmployeeModel(String name, String workEmail, OfficeModel office, boolean active) {
        this.name=name;
        this.workEmail=workEmail;
        this.office=office;

    }


}
