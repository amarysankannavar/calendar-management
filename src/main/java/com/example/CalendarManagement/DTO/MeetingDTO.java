package com.example.CalendarManagement.DTO;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MeetingDTO {


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

    private boolean isActive;



}
