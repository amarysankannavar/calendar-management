package com.example.CalendarManagement.model;

import javax.persistence.*;

@Entity
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

    public MeetingStatusModel() {
    }

    public MeetingStatusModel(EmployeeModel employee, MeetingModel meeting, String status) {
        this.employee = employee;
        this.meeting = meeting;
        this.status = status;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public EmployeeModel getEmployee() {
        return employee;
    }

    public void setEmployee(EmployeeModel employee) {
        this.employee = employee;
    }

    public MeetingModel getMeeting() {
        return meeting;
    }

    public void setMeeting(MeetingModel meeting) {
        this.meeting = meeting;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
