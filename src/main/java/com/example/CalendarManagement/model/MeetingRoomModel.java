package com.example.CalendarManagement.model;

import javax.persistence.*;

@Entity
public class MeetingRoomModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int roomId;
    private String roomName;
    private String roomLocation;

    @ManyToOne
    @JoinColumn(name = "office_id", nullable = false)
    private OfficeModel office;

    private boolean available=true;

    public MeetingRoomModel(){

    }

    public MeetingRoomModel(String roomName,String roomLocation, OfficeModel office){
        this.roomName=roomName;
        this.roomLocation=roomLocation;
        this.office=office;
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

    public OfficeModel getOffice(){
        return office;
    }

    public void setOffice(OfficeModel office){
        this.office =office;
    }
    public boolean isAvailable(){
        return available;
    }

    public void setAvailable(boolean available){
        this.available=available;
    }

    public String getRoomLocation(){
        return roomLocation;
    }

    public void setRoomLocation(String roomLocation){
        this.roomLocation=roomLocation;
    }

}
