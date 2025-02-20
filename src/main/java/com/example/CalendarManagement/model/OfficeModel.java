package com.example.CalendarManagement.model;

import javax.persistence.*;

@Entity
public class OfficeModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String name;
    private String officeLoc;

    public OfficeModel() {
    }

    public OfficeModel(String name, String officeLoc) {
        this.name = name;
        this.officeLoc = officeLoc;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getOfficeLoc() {
        return officeLoc;
    }

    public void setOfficeLoc(String officeLoc) {
        this.officeLoc = officeLoc;
    }
}
