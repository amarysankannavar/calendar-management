package com.example.CalendarManagement.DTO;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

public class MeetingRoomDTO {

    private int roomId;

    @NotBlank(message = "Room Name can not be empty.")
    private String roomName;

    @NotBlank(message = "Room Location can not be empty.")
    private String roomLocation;

    @NotNull(message = "Office ID cannot be empty.")
    private int officeId;


    private boolean isAvailable;


    public MeetingRoomDTO(int roomId, String roomName, String roomLocation, int officeId, boolean isAvailable) {
        this.roomId=roomId;
        this.roomName=roomName;
        this.roomLocation=roomLocation;
        this.officeId=officeId;
        this.isAvailable=isAvailable;
    }

    public MeetingRoomDTO() {
    }

    public int getRoomId(){
        return roomId;
    }

    public void setRoomId(int roomId){
        this.roomId=roomId;
    }

    public String getRoomName(){
        return roomName;
    }

    public void setRoomName(String roomName){
        this.roomName=roomName;
    }

    public int getOfficeId(){
        return officeId;
    }

    public void setOfficeId(int officeId){
        this.officeId=officeId;
    }

    public boolean isAvailable(){
        return isAvailable;
    }

    public void setAvailable(boolean isAvailable){
        this.isAvailable=isAvailable;
    }

    public String getRoomLocation(){
        return roomLocation;
    }

    public void setRoomLocation(String roomLocation){
        this.roomLocation=roomLocation;
    }
}
