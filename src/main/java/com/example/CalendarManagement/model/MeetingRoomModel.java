package com.example.CalendarManagement.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
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


    public MeetingRoomModel(String roomName,String roomLocation, OfficeModel office){
        this.roomName=roomName;
        this.roomLocation=roomLocation;
        this.office=office;
    }

}
