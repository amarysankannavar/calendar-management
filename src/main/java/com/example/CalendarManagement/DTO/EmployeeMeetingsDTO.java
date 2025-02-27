package com.example.CalendarManagement.DTO;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;


@Getter
@Setter
public class EmployeeMeetingsDTO {
    private LocalDate fromDate;
    private LocalDate toDate;
    boolean currentWeek;

}
