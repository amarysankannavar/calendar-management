package com.example.CalendarManagement.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class MeetingStatusModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;  // Auto-generated ID for uniqueness

    @ManyToOne
    @JoinColumn(name = "empId", nullable = false)
    private EmployeeModel employee;

    @ManyToOne
    @JoinColumn(name = "meeting_id", nullable = false)
    private MeetingModel meeting;

    private String status;


    public MeetingStatusModel(EmployeeModel employee, MeetingModel meeting, String status) {
        this.employee = employee;
        this.meeting = meeting;
        this.status = status;
    }



}
