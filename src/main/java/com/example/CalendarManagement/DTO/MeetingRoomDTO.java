package com.example.CalendarManagement.DTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class MeetingRoomDTO {

    private int roomId;

    @NotBlank(message = "Room Name can not be empty.")
    private String roomName;

    @NotBlank(message = "Room Location can not be empty.")
    private String roomLocation;

    @NotNull(message = "Office ID cannot be empty.")
    private int officeId;


    private boolean isAvailable;


}
