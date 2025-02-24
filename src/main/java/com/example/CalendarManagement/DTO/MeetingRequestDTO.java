package com.example.CalendarManagement.DTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MeetingRequestDTO {
    @NotNull(message = "employeeIds cannot be null.")
    private List<Integer> employeeIds;
    @NotNull(message = "startTime cannot be null.")
    private LocalTime startTime;
    @NotNull(message = "endTime cannot be null.")
    private LocalTime endTime;
    @NotNull(message = "date cannot be null.")
    private LocalDate date;

    private int roomId;


}
