package com.example.CalendarManagement.DTO;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public class MeetingRequestDTO {
    @NotNull(message = "employeeIds cannot be null.")
    private List<Integer> employeeIds;
    @NotNull(message = "startTime cannot be null.")
    private LocalTime startTime;
    @NotNull(message = "endTime cannot be null.")
    private LocalTime endTime;
    @NotNull(message = "date cannot be null.")
    private LocalDate date;

    // Getters and Setters
    public List<Integer> getEmployeeIds() {
        return employeeIds;
    }

    public void setEmployeeIds(List<Integer> employeeIds) {
        this.employeeIds = employeeIds;
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

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }
}
