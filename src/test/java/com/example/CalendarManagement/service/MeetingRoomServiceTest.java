package com.example.CalendarManagement.service;

import com.example.CalendarManagement.DTO.MeetingRoomDTO;
import com.example.CalendarManagement.Exception.DataStorageException;
import com.example.CalendarManagement.Exception.RoomNotFoundException;
import com.example.CalendarManagement.model.MeetingRoomModel;
import com.example.CalendarManagement.model.OfficeModel;
import com.example.CalendarManagement.repository.MeetingRepo;
import com.example.CalendarManagement.repository.MeetingRoomRepo;
import com.example.CalendarManagement.repository.OfficeRepo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class MeetingRoomServiceTest {

    @Mock
    private MeetingRoomRepo meetingRoomRepo;

    @Mock
    private OfficeRepo officeRepo;

    @Mock
    private MeetingRepo meetingRepo;

    @InjectMocks
    private MeetingRoomService meetingRoomService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getMeetingRooms_returnsAllRooms() {
        OfficeModel office = new OfficeModel();
        office.setId(1);
        MeetingRoomModel room1 = new MeetingRoomModel("Room A", "First Floor", office);
        MeetingRoomModel room2 = new MeetingRoomModel("Room B", "Second Floor", office);
        room1.setAvailable(true);
        room2.setAvailable(false);
        when(meetingRoomRepo.findAll()).thenReturn(Arrays.asList(room1, room2));

        List<MeetingRoomDTO> result = meetingRoomService.getMeetingRooms();

        assertThat(result).hasSize(2);
        assertEquals("Room A", result.get(0).getRoomName());
        assertEquals("Room B", result.get(1).getRoomName());
    }

    @Test
    void addMeetingRoom_givenValidDetails_savesSuccessfully() {
        OfficeModel office = new OfficeModel();
        office.setId(1);
        MeetingRoomDTO roomDTO = new MeetingRoomDTO(0, "Room X", "Third Floor", 1, true);
        when(officeRepo.findById(1)).thenReturn(Optional.of(office));

        assertDoesNotThrow(() -> meetingRoomService.addMeetingRoom(roomDTO));
        verify(meetingRoomRepo, times(1)).save(any(MeetingRoomModel.class));
    }

    @Test
    void addMeetingRoom_givenInvalidOffice_throwsException() {
        MeetingRoomDTO roomDTO = new MeetingRoomDTO(0, "Room X", "Third Floor", 99, true);
        when(officeRepo.findById(99)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> meetingRoomService.addMeetingRoom(roomDTO));
    }

    @Test
    void getMeetingRoomById_givenValidId_returnsRoom() {
        OfficeModel office = new OfficeModel();
        office.setId(1);
        MeetingRoomModel room = new MeetingRoomModel("Room Y", "Ground Floor", office);
        room.setRoomId(5);
        room.setAvailable(true);

        when(meetingRoomRepo.findById(5)).thenReturn(Optional.of(room));

        MeetingRoomDTO result = meetingRoomService.getMeetingRoomById(5);

        assertNotNull(result);
        assertEquals("Room Y", result.getRoomName());
        assertTrue(result.isAvailable());
    }

    @Test
    void getMeetingRoomById_givenInvalidId_throwsException() {
        when(meetingRoomRepo.findById(99)).thenReturn(Optional.empty());

        assertThrows(RoomNotFoundException.class, () -> meetingRoomService.getMeetingRoomById(99));
    }

    @Test
    void deleteMeetingRoom_givenValidRoomId_deactivatesRoom() {
        MeetingRoomModel room = new MeetingRoomModel();
        room.setRoomId(10);
        room.setAvailable(true);
        when(meetingRoomRepo.findById(10)).thenReturn(Optional.of(room));

        meetingRoomService.deleteMeetingRoom(10);

        assertFalse(room.isAvailable());
        verify(meetingRoomRepo, times(1)).save(room);
    }

    @Test
    void deleteMeetingRoom_givenInvalidRoomId_throwsException() {
        when(meetingRoomRepo.findById(99)).thenReturn(Optional.empty());

        assertThrows(RoomNotFoundException.class, () -> meetingRoomService.deleteMeetingRoom(99));
    }

    @Test
    void updateMeetingRoomAvailability_givenValidId_updatesAvailability() {
        MeetingRoomModel room = new MeetingRoomModel();
        room.setRoomId(20);
        room.setAvailable(false);
        when(meetingRoomRepo.findById(20)).thenReturn(Optional.of(room));

        meetingRoomService.updateMeetingRoomAvailability(20, true);

        assertTrue(room.isAvailable());
        verify(meetingRoomRepo, times(1)).save(room);
    }

    @Test
    void updateMeetingRoomAvailability_givenInvalidId_throwsException() {
        when(meetingRoomRepo.findById(99)).thenReturn(Optional.empty());

        assertThrows(RoomNotFoundException.class, () -> meetingRoomService.updateMeetingRoomAvailability(99, true));
    }
}