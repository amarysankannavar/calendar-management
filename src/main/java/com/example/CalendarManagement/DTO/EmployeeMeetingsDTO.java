package com.example.CalendarManagement.DTO;

import java.time.LocalDate;

public class EmployeeMeetingsDTO {
    private LocalDate fromDate;
    private LocalDate toDate;

    // Getters and Setters
    public LocalDate getFromDate() {
        return fromDate;
    }

    public void setFromDate(LocalDate fromDate) {
        this.fromDate = fromDate;
    }

    public LocalDate getToDate() {
        return toDate;
    }

    public void setToDate(LocalDate toDate) {
        this.toDate = toDate;
    }
}
