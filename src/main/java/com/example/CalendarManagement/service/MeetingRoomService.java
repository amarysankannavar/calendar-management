package com.example.CalendarManagement.service;

import com.example.CalendarManagement.DTO.MeetingRoomDTO;
import com.example.CalendarManagement.Exception.RoomNotFoundException;
import com.example.CalendarManagement.model.MeetingRoomModel;
import com.example.CalendarManagement.model.OfficeModel;
import com.example.CalendarManagement.repository.MeetingRoomRepo;
import com.example.CalendarManagement.repository.OfficeRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class MeetingRoomService {

    @Autowired
    private MeetingRoomRepo meetingRoomRepo;

    @Autowired
    private OfficeRepo officeRepo; // Inject OfficeRepo to fetch OfficeModel

    public List<MeetingRoomDTO> getMeetingRooms() {
        return meetingRoomRepo.findAll().stream()
                .map(room -> new MeetingRoomDTO(
                        room.getRoomId(),
                        room.getRoomName(),
                        room.getRoomLocation(),
                        room.getOffice().getId(),
                        room.isAvailable()
                ))
                .collect(Collectors.toList());
    }

    public void addMeetingRoom(MeetingRoomDTO roomDTO) {
        if (roomDTO.getRoomId() != 0 && meetingRoomRepo.existsById(roomDTO.getRoomId())) {
            throw new IllegalArgumentException("Room ID already exists.");
        }

        // Validate name and location
        if (roomDTO.getRoomName() == null || roomDTO.getRoomName().trim().isEmpty()) {
            throw new IllegalArgumentException("Meeting room name cannot be empty.");
        }

        if (roomDTO.getRoomLocation() == null || roomDTO.getRoomLocation().trim().isEmpty()) {
            throw new IllegalArgumentException("Meeting room location cannot be empty.");
        }

        // Fetch office based on officeId
        OfficeModel office = officeRepo.findById(roomDTO.getOfficeId())
                .orElseThrow(() -> new IllegalArgumentException("Invalid office ID"));

        // Create and save new MeetingRoom
        MeetingRoomModel room = new MeetingRoomModel(roomDTO.getRoomName(), roomDTO.getRoomLocation(), office);
        meetingRoomRepo.save(room);
    }

    public MeetingRoomDTO getMeetingRoomById(int meetingRoomId) {
        MeetingRoomModel meetingRoom = meetingRoomRepo.findById(meetingRoomId)
                .orElseThrow(() -> new RoomNotFoundException("Meeting Room not found"));

        // Map and return MeetingRoomDTO with officeId
        return new MeetingRoomDTO(
                meetingRoom.getRoomId(),
                meetingRoom.getRoomName(),
                meetingRoom.getRoomLocation(),
                meetingRoom.getOffice().getId(),
                meetingRoom.isAvailable()
        );
    }

    public void deleteMeetingRoom(int roomId) {
        MeetingRoomModel room = meetingRoomRepo.findById(roomId)
                .orElseThrow(() -> new RoomNotFoundException("Meeting Room not found"));

        meetingRoomRepo.delete(room);
    }

    public void updateMeetingRoomAvailability(int roomId, boolean availability) {
        MeetingRoomModel room = meetingRoomRepo.findById(roomId)
                .orElseThrow(() -> new RoomNotFoundException("Meeting Room not found"));
        if (room.isAvailable() == availability) {
            return;
        }

        room.setAvailable(availability);
        meetingRoomRepo.save(room);
    }
}
