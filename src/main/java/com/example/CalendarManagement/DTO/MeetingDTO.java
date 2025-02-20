package com.example.CalendarManagement.DTO;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public class MeetingDTO {

    private boolean isActive;
    private int meetingId;

    @NotBlank(message = "Meeting description cannot be empty.")
    private String description;

    @NotBlank(message = "Meeting agenda cannot be empty.")
    private String agenda;

    @NotNull(message = "Room ID cannot be null.")
    private int roomId;

    @NotNull(message = "Date cannot be null.")
    private LocalDate date;

    @NotNull(message = "Start time cannot be null.")
    private LocalTime startTime;

    @NotNull(message = "End time cannot be null.")
    private LocalTime endTime;

    // No-args constructor for Jackson deserialization
    public MeetingDTO() {}

    public MeetingDTO(int meetingId, String description, String agenda, int roomId, LocalDate date,
                      LocalTime startTime, LocalTime endTime, boolean isActive) {
        this.meetingId = meetingId;
        this.description = description;
        this.agenda = agenda;
        this.roomId = roomId;
        this.date = date;
        this.startTime = startTime;
        this.endTime = endTime;
        this.isActive = isActive;
    }

    @NotNull(message = "Employee IDs cannot be null.")
    private List<Integer> employeeIds;
    // Getters and Setters
    public int getMeetingId() {
        return meetingId;
    }

    public void setMeetingId(int meetingId) {
        this.meetingId = meetingId;
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

    public int getRoomId() {
        return roomId;
    }

    public void setRoomId(int roomId) {
        this.roomId = roomId;
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
        this.isActive = active;
    }
}
