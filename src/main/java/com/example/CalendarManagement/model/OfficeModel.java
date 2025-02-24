package com.example.CalendarManagement.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class OfficeModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String name;
    private String officeLoc;


    public OfficeModel(String name, String officeLoc) {
        this.name = name;
        this.officeLoc = officeLoc;
    }
}
