package com.example.CalendarManagement.DTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MeetingsDTO {

    private int meetingId;
    private String description;
    private String agenda;
    private int roomId;
    private LocalDate date;
    private LocalTime startTime;
    private LocalTime endTime;
    private boolean isActive;
   // List<Integer> employees;




}
