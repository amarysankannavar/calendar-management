package com.example.CalendarManagement.service;

import com.example.CalendarManagement.DTO.MeetingDTO;
import com.example.CalendarManagement.Exception.MeetingNotFoundException;
import com.example.CalendarManagement.Exception.RoomNotFoundException;
import com.example.CalendarManagement.model.MeetingModel;
import com.example.CalendarManagement.model.MeetingRoomModel;
import com.example.CalendarManagement.repository.EmployeeRepo;
import com.example.CalendarManagement.repository.MeetingRepo;
import com.example.CalendarManagement.repository.MeetingRoomRepo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MeetingServiceTest {

    @Mock
    private MeetingRepo meetingRepo;

    @Mock
    private MeetingRoomRepo meetingRoomRepo;

    @Mock
    private EmployeeRepo employeeRepo;

    @InjectMocks
    private MeetingService meetingService;




    private MeetingModel meeting;
    private MeetingRoomModel meetingRoom;
    private MeetingDTO meetingDTO;


    @BeforeEach
    void setUp() {
        meetingRoom = new MeetingRoomModel();
        meetingRoom.setRoomId(1);

        meeting = new MeetingModel("Project Update", "Discuss Q1 goals", meetingRoom, LocalDate.now(),
                LocalTime.of(10, 0), LocalTime.of(11, 0), true);
        meeting.setId(1);

        meetingDTO = new MeetingDTO(1, "Project Update", "Discuss Q1 goals", 1, LocalDate.now(),
                LocalTime.of(10, 0), LocalTime.of(11, 0), true);
    }

    @Test
    void getMeetings_whenMeetingsExists_returnsMeetingsList() {
        when(meetingRepo.findAll()).thenReturn(List.of(meeting));
        List<MeetingDTO> meetings = meetingService.getMeetings();
        assertEquals(1, meetings.size());
        assertEquals("Project Update", meetings.get(0).getDescription());
    }

    @Test
    void getMeetingById_givenMeetingId_returnsMeetingDetails() {
        when(meetingRepo.findById(1)).thenReturn(Optional.of(meeting));
        MeetingDTO foundMeeting = meetingService.getMeetingById(1);
        assertEquals("Project Update", foundMeeting.getDescription());
    }

    @Test
    void getMeetingById_givenNonExistingMeetingId_throwsMeetingNotFoundException() {
        when(meetingRepo.findById(1)).thenReturn(Optional.empty());
        assertThrows(MeetingNotFoundException.class, () -> meetingService.getMeetingById(1));
    }

    @Test
    void addMeeting_givenMeetingDetails_addMeetings() {
        when(meetingRoomRepo.findById(1)).thenReturn(Optional.of(meetingRoom));
        when(meetingRepo.save(any(MeetingModel.class))).thenReturn(meeting);
        assertDoesNotThrow(() -> meetingService.addMeeting(meetingDTO));
    }

    @Test
    void addMeeting_givenInvaidRoomId_thorwsRoomNotFoundException() {
        when(meetingRoomRepo.findById(1)).thenReturn(Optional.empty());
        assertThrows(RoomNotFoundException.class, () -> meetingService.addMeeting(meetingDTO));
    }

    @Test
    void deleteMeeting_givenMeetingId_deletesMeeting() {
        when(meetingRepo.findById(1)).thenReturn(Optional.of(meeting));
        doNothing().when(meetingRepo).delete(meeting);
        assertTrue(meetingService.deleteMeeting(1));
    }

    @Test
    void deleteMeeting_givenInvalidMeetingId_throwsMeetingNotFoundException() {
        when(meetingRepo.findById(1)).thenReturn(Optional.empty());
        assertThrows(MeetingNotFoundException.class, () -> meetingService.deleteMeeting(1));
    }

    @Test
    void cancelMeeting_givenMeetingId_setActiveFalse() {
        when(meetingRepo.findById(1)).thenReturn(Optional.of(meeting));
        meeting.setActive(false);
        when(meetingRepo.save(meeting)).thenReturn(meeting);
        assertTrue(meetingService.cancelMeeting(1));
    }

    @Test
    void cancelMeeting_givenInvalidMeetingId_throwsMeetingNotFoundException() {
        when(meetingRepo.findById(1)).thenReturn(Optional.empty());
        assertThrows(MeetingNotFoundException.class, () -> meetingService.cancelMeeting(1));
    }

}
