package com.example.CalendarManagement.DTO;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;


@Getter
@Setter
public class ScheduleMeetingDTO {
    @NotNull(message = "Employee Ids cannot be null.")
    private List<Integer> employeeIds;
    @NotNull(message = "Description can not be null.")
    private String description;
    @NotNull(message = "Agenda cannot be null.")
    private String agenda;
    @NotNull(message = "startTime cannot be null.")
    private LocalTime startTime;
    @NotNull(message = "endTime cannot be null.")
    private LocalTime endTime;
    @NotNull(message = "date cannot be null.")
    private LocalDate date;
    private int roomId;

}
