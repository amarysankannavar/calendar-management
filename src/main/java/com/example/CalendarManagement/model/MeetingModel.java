package com.example.CalendarManagement.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class MeetingModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String description;
    private String agenda;

    @ManyToOne(fetch = FetchType.LAZY)  // Establishing a ManyToOne relationship
    @JoinColumn(name = "roomId")  // Foreign key column name
    private MeetingRoomModel meetingRoom;  // Room for the meeting

    private LocalDate date;
    private LocalTime startTime;
    private LocalTime endTime;

    @Column(columnDefinition = "BOOLEAN DEFAULT true")
    private boolean isActive = true;


    public MeetingModel(String description, String agenda, MeetingRoomModel meetingRoom,
                        LocalDate date, LocalTime startTime, LocalTime endTime, boolean isActive) {
        this.description = description;
        this.agenda = agenda;
        this.meetingRoom = meetingRoom;
        this.date = date;
        this.startTime = startTime;
        this.endTime = endTime;
        this.isActive = isActive;
    }

}
