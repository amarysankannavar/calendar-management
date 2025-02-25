package com.example.CalendarManagement.service;

import com.example.CalendarManagement.DTO.MeetingRoomDTO;
import com.example.CalendarManagement.Exception.DataStorageException;
import com.example.CalendarManagement.Exception.RoomNotFoundException;
import com.example.CalendarManagement.model.MeetingModel;
import com.example.CalendarManagement.model.MeetingRoomModel;
import com.example.CalendarManagement.model.OfficeModel;
import com.example.CalendarManagement.repository.MeetingRepo;
import com.example.CalendarManagement.repository.MeetingRoomRepo;
import com.example.CalendarManagement.repository.MeetingStatusRepo;
import com.example.CalendarManagement.repository.OfficeRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class MeetingRoomService {

    @Autowired
    private MeetingRoomRepo meetingRoomRepo;

    @Autowired
    private OfficeRepo officeRepo; // Inject OfficeRepo to fetch OfficeModel

    @Autowired
    private MeetingRepo meetingRepo;

    @Autowired
    private MeetingStatusRepo meetingStatusRepo;

    @Autowired
    private MeetingService meetingService;

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
        // Fetch office based on officeId
        OfficeModel office = officeRepo.findById(roomDTO.getOfficeId())
                .orElseThrow(() -> new IllegalArgumentException("Invalid office ID"));

        // Create and save new MeetingRoom
        try{
            MeetingRoomModel room = new MeetingRoomModel(roomDTO.getRoomName(), roomDTO.getRoomLocation(), office);
            meetingRoomRepo.save(room);
        } catch (DataIntegrityViolationException e){
           throw e;
        }  catch(Exception e) {
            throw new DataStorageException("Failed to add the Meeting Room.");
        }
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

    @Transactional
    public void deleteMeetingRoom(int roomId) {
        MeetingRoomModel room = meetingRoomRepo.findById(roomId)
                .orElseThrow(() -> new RoomNotFoundException("Meeting Room not found"));

           List<MeetingModel> meetings = meetingRepo.findByMeetingRoom(room);

        if (!meetings.isEmpty()) {
            for (MeetingModel meeting : meetings) {
                // Delete all meeting status records linked to this meeting
                meetingService.cancelMeeting(meeting.getId());

                // Set the meeting as inactive

                meetingRepo.save(meeting);
            }
        }
        room.setAvailable(false);
       try{
           meetingRoomRepo.save(room);
       } catch (Exception e) {
           throw new DataStorageException("meeting room not stored to database.");
       }


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
