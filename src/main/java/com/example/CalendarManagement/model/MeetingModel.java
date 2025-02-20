package com.example.CalendarManagement.model;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalTime;

@Entity
public class MeetingModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String description;
    private String agenda;

    @ManyToOne(fetch = FetchType.LAZY)  // Establishing a ManyToOne relationship
    @JoinColumn(name = "roomId")  // Foreign key column name
    private MeetingRoomModel meetingRoom;  // Room for the meeting

    private LocalDate date;
    private LocalTime startTime;
    private LocalTime endTime;

    @Column(columnDefinition = "BOOLEAN DEFAULT true")
    private boolean isActive = true;

    // Constructors
    public MeetingModel() {}

    public MeetingModel(String description, String agenda, MeetingRoomModel meetingRoom,
                        LocalDate date, LocalTime startTime, LocalTime endTime, boolean isActive) {
        this.description = description;
        this.agenda = agenda;
        this.meetingRoom = meetingRoom;
        this.date = date;
        this.startTime = startTime;
        this.endTime = endTime;
        this.isActive = isActive;
    }

    public MeetingModel(int meetingId, String description, String agenda,MeetingRoomModel meetingRoom,String date, String startTime, String endTime, boolean isActive) {
        this.id = meetingId;
        this.description = description;
        this.agenda = agenda;
        this.meetingRoom = meetingRoom;
        this.date= LocalDate.parse(date);
        this.startTime = LocalTime.parse(startTime);
        this.endTime = LocalTime.parse(endTime);
        this.isActive = isActive;
    }


    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getAgenda() {
        return agenda;
    }

    public void setAgenda(String agenda) {
        this.agenda = agenda;
    }

    public MeetingRoomModel getMeetingRoom() {
        return meetingRoom;
    }

    public void setMeetingRoom(MeetingRoomModel meetingRoom) {
        this.meetingRoom = meetingRoom;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalTime startTime) {
        this.startTime = startTime;
    }

    public LocalTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalTime endTime) {
        this.endTime = endTime;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }
}
