package com.example.CalendarManagement.service;

import com.example.CalendarManagement.DTO.MeetingRequestDTO;
import com.example.CalendarManagement.DTO.ScheduleMeetingDTO;
import com.example.CalendarManagement.Exception.EmployeeNotFoundException;
import com.example.CalendarManagement.Exception.MeetingNotFoundException;
import com.example.CalendarManagement.generated.MeetingResponse;
import com.example.CalendarManagement.model.MeetingModel;
import com.example.CalendarManagement.model.MeetingRoomModel;
import com.example.CalendarManagement.repository.EmployeeRepo;
import com.example.CalendarManagement.repository.MeetingRepo;
import com.example.CalendarManagement.repository.MeetingStatusRepo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class MeetingServiceTest {

    @InjectMocks
    private MeetingService meetingService;

    @Mock
    private MeetingRepo meetingRepo;

    @Mock
    private MeetingStatusRepo meetingStatusRepo;

    @Mock
    private EmployeeRepo employeeRepo;

    @BeforeEach
    void setUp() {
        // Setup can be done here if needed
    }

    @Test
    void getMeetings_ReturnsMeetingList() {
        when(meetingRepo.findAll()).thenReturn(Collections.emptyList());
        assertTrue(meetingService.getMeetings().isEmpty());
    }

    @Test
    void getMeetingById_ExistingMeeting_ReturnsMeetingDTO() {
        MeetingModel meeting = new MeetingModel();
        MeetingRoomModel meetingRoom = new MeetingRoomModel();
        meetingRoom.setRoomId(101);
        meeting.setMeetingRoom(meetingRoom);

        when(meetingRepo.findById(1)).thenReturn(Optional.of(meeting));

        assertNotNull(meetingService.getMeetingById(1));
    }

    @Test
    void getMeetingById_NonExistingMeeting_ThrowsException() {
        when(meetingRepo.findById(1)).thenReturn(Optional.empty());
        assertThrows(MeetingNotFoundException.class, () -> meetingService.getMeetingById(1));
    }

    @Test
    void cancelMeeting_ExistingMeeting_SoftDeletesMeeting() {
        // Arrange
        MeetingModel meeting = new MeetingModel();
        meeting.setActive(true);

        when(meetingRepo.findById(1)).thenReturn(Optional.of(meeting));

        // Act
        boolean result = meetingService.cancelMeeting(1);

        // Assert
        assertTrue(result);
        assertFalse(meeting.isActive()); // Ensure soft delete
        verify(meetingStatusRepo, times(1)).deleteByMeeting(meeting); // Ensure meeting status is deleted
        verify(meetingRepo, times(1)).save(meeting); // Ensure meeting is saved after soft delete
    }

    @Test
    void cancelMeeting_NonExistingMeeting_ThrowsException() {
        when(meetingRepo.findById(1)).thenReturn(Optional.empty());
        assertThrows(MeetingNotFoundException.class, () -> meetingService.cancelMeeting(1));
    }

    @Test
    void canSchedule_InvalidEmployee_ThrowsException() {
        MeetingRequestDTO requestDTO = new MeetingRequestDTO();
        requestDTO.setEmployeeIds(Collections.singletonList(99));

        when(employeeRepo.findById(any())).thenReturn(Optional.empty());

        assertThrows(EmployeeNotFoundException.class, () -> meetingService.canSchedule(requestDTO));
    }

    @Test
    void scheduleMeeting_ValidRequest_ReturnsMeetingResponse() {
        ScheduleMeetingDTO requestDTO = new ScheduleMeetingDTO();
        MeetingResponse response = new MeetingResponse();
        response.setScheduledMeetingId(1);
        response.setAvailableRoomId(101);

        MeetingService spyService = Mockito.spy(meetingService);
        doReturn(response).when(spyService).scheduleMeeting(any(ScheduleMeetingDTO.class));

        MeetingResponse result = spyService.scheduleMeeting(requestDTO);
        assertEquals(1, result.getScheduledMeetingId());
        assertEquals(101, result.getAvailableRoomId());
    }
}
