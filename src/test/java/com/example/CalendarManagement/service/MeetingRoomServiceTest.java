package com.example.CalendarManagement.service;

import com.example.CalendarManagement.DTO.MeetingRoomDTO;
import com.example.CalendarManagement.Exception.RoomNotFoundException;
import com.example.CalendarManagement.model.MeetingRoomModel;
import com.example.CalendarManagement.model.OfficeModel;
import com.example.CalendarManagement.repository.MeetingRoomRepo;
import com.example.CalendarManagement.repository.OfficeRepo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class MeetingRoomServiceTest {

    @Mock
    private MeetingRoomRepo meetingRoomRepo;

    @Mock
    private OfficeRepo officeRepo;

    @InjectMocks
    private MeetingRoomService meetingRoomService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void addMeetingRoom_givenValidMeetingRoomDetails_returnsSuccess() {
        MeetingRoomDTO roomDTO = new MeetingRoomDTO(1, "Conference A", "Floor 2", 101, true);
       // MeetingRoomDTO roomDTO = new MeetingRoomDTO(1, "Conference A", "Floor 2", 101, true);
        OfficeModel office = new OfficeModel(); // Mock OfficeModel
        office.setId(101);
        MeetingRoomModel roomModel = new MeetingRoomModel(roomDTO.getRoomName(), roomDTO.getRoomLocation(), office);

        when(meetingRoomRepo.existsById(roomDTO.getRoomId())).thenReturn(false);
        when(officeRepo.findById(roomDTO.getOfficeId())).thenReturn(Optional.of(office));


        assertDoesNotThrow(() -> meetingRoomService.addMeetingRoom(roomDTO));

        verify(meetingRoomRepo, times(1)).save(any(MeetingRoomModel.class));
    }

    @Test
    void addMeetingRoom_givenDuplicateRoomId_throwsException() {
        MeetingRoomDTO roomDTO = new MeetingRoomDTO(1, "Conference B", "Floor 3", 102, true);

        when(meetingRoomRepo.existsById(roomDTO.getRoomId())).thenReturn(true);

        Exception exception = assertThrows(IllegalArgumentException.class, () -> meetingRoomService.addMeetingRoom(roomDTO));
        assertEquals("Room ID already exists.", exception.getMessage());

        verify(meetingRoomRepo, never()).save(any());
    }

    @Test
    void addMeetingRoom_givenEmptyRoomName_throwsValidationError() {
        MeetingRoomDTO roomDTO = new MeetingRoomDTO(1, "", "Floor 2", 101, true);

        Exception exception = assertThrows(IllegalArgumentException.class, () -> meetingRoomService.addMeetingRoom(roomDTO));
        assertEquals("Meeting room name cannot be empty.", exception.getMessage());

        verify(meetingRoomRepo, never()).save(any());
    }

    @Test
    void addMeetingRoom_givenEmptyRoomLocation_throwsValidationError() {
        MeetingRoomDTO roomDTO = new MeetingRoomDTO(1, "Conference A", "", 101, true);

        Exception exception = assertThrows(IllegalArgumentException.class, () -> meetingRoomService.addMeetingRoom(roomDTO));
        assertEquals("Meeting room location cannot be empty.", exception.getMessage());

        verify(meetingRoomRepo, never()).save(any());
    }

    @Test
    void deleteMeetingRoom_givenExistingRoomId_deletesRoom() {
        int roomId = 1;
        OfficeModel office2 = new OfficeModel(); // Mock OfficeModel
        office2.setId(101);
        MeetingRoomModel room = new MeetingRoomModel("Conference A", "Floor 2", office2);
        room.setRoomId(roomId);

        when(meetingRoomRepo.findById(roomId)).thenReturn(Optional.of(room));

        meetingRoomService.deleteMeetingRoom(roomId);

        verify(meetingRoomRepo, times(1)).findById(roomId);
        verify(meetingRoomRepo, times(1)).delete(room); 
    }


    @Test
    void deleteMeetingRoom_givenNonExistingRoomId_throwsNotFoundException() {
        int roomId = 2;
        when(meetingRoomRepo.findById(roomId)).thenReturn(Optional.empty());

        Exception exception = assertThrows(RoomNotFoundException.class, () -> meetingRoomService.deleteMeetingRoom(roomId));
        assertEquals("Meeting Room not found", exception.getMessage());

        verify(meetingRoomRepo, times(1)).findById(roomId);
        verify(meetingRoomRepo, never()).save(any());
    }

    @Test
    void getMeetingRooms_whenRoomsExist_returnRoomList() {
        OfficeModel office3 = new OfficeModel(); // Mock OfficeModel
        office3.setId(103);
        OfficeModel office4 = new OfficeModel(); // Mock OfficeModel
        office4.setId(104);
        MeetingRoomModel room1 = new MeetingRoomModel("Conference A", "Floor 2", office3);
        MeetingRoomModel room2 = new MeetingRoomModel("Conference B", "Floor 3", office4);
        List<MeetingRoomModel> roomModels = Arrays.asList(room1, room2);

        when(meetingRoomRepo.findAll()).thenReturn(roomModels);

        List<MeetingRoomDTO> result = meetingRoomService.getMeetingRooms();

        assertThat(result)
                .hasSize(2)
                .extracting(MeetingRoomDTO::getRoomName, MeetingRoomDTO::getRoomLocation, MeetingRoomDTO::getOfficeId)
                .containsExactlyInAnyOrder(
                        tuple("Conference A", "Floor 2", office3.getId()),
                        tuple("Conference B", "Floor 3", office4.getId())
                );

        verify(meetingRoomRepo, times(1)).findAll();
    }

    @Test
    void getMeetingRoomById_givenExistingRoomId_returnsRoomInfo() {
        int roomId = 1;
        OfficeModel office6 = new OfficeModel(); // Mock OfficeModel
        office6.setId(106);
        MeetingRoomModel room = new MeetingRoomModel("Conference A", "Floor 2", office6);
        room.setRoomId(roomId);
        room.setAvailable(true);

        when(meetingRoomRepo.findById(roomId)).thenReturn(Optional.of(room));

        MeetingRoomDTO result = meetingRoomService.getMeetingRoomById(roomId);

        assertThat(result).isNotNull();
        assertThat(result.getRoomId()).isEqualTo(roomId);
        assertThat(result.getRoomName()).isEqualTo("Conference A");
        assertThat(result.isAvailable()).isTrue();

        verify(meetingRoomRepo, times(1)).findById(roomId);
    }

    @Test
    void getMeetingRoomById_givenNonExistingRoomId_throwsNotFoundException() {
        int roomId = 3;
        when(meetingRoomRepo.findById(roomId)).thenReturn(Optional.empty());

        Exception exception = assertThrows(RoomNotFoundException.class, () -> meetingRoomService.getMeetingRoomById(roomId));
        assertEquals("Meeting Room not found", exception.getMessage());

        verify(meetingRoomRepo, times(1)).findById(roomId);
    }

    @Test
    void updateMeetingRoomAvailability_givenExistingRoomId_setsAvailableTrue() {
        int roomId = 1;
        OfficeModel office5 = new OfficeModel(); // Mock OfficeModel
        office5.setId(105);
        MeetingRoomModel room = new MeetingRoomModel("Conference A", "Floor 2", office5);
        room.setRoomId(roomId);
        room.setAvailable(false);

        when(meetingRoomRepo.findById(roomId)).thenReturn(Optional.of(room));
        when(meetingRoomRepo.save(any(MeetingRoomModel.class))).thenReturn(room);

        Map<String, Boolean> requestBody = new HashMap<>();
        requestBody.put("availability", true);

        meetingRoomService.updateMeetingRoomAvailability(roomId, requestBody.get("availability"));

        assertTrue(room.isAvailable());

        verify(meetingRoomRepo, times(1)).findById(roomId);
        verify(meetingRoomRepo, times(1)).save(room);
    }

    @Test
    void updateMeetingRoomAvailability_givenExistingRoomId_setsAvailableFalse() {
        int roomId = 1;
        OfficeModel office7 = new OfficeModel(); // Mock OfficeModel
        office7.setId(107);
        MeetingRoomModel room = new MeetingRoomModel("Conference A", "Floor 2", office7);
        room.setRoomId(roomId);
        room.setAvailable(true);

        when(meetingRoomRepo.findById(roomId)).thenReturn(Optional.of(room));
        when(meetingRoomRepo.save(any(MeetingRoomModel.class))).thenReturn(room);

        Map<String, Boolean> requestBody = new HashMap<>();
        requestBody.put("availability", false);

        meetingRoomService.updateMeetingRoomAvailability(roomId, requestBody.get("availability"));

        assertFalse(room.isAvailable());

        verify(meetingRoomRepo, times(1)).findById(roomId);
        verify(meetingRoomRepo, times(1)).save(room);
    }

    @Test
    void updateMeetingRoomAvailability_givenNonExistingRoomId_throwsNotFoundException() {
        int roomId = 2;
        when(meetingRoomRepo.findById(roomId)).thenReturn(Optional.empty());

        Map<String, Boolean> requestBody = new HashMap<>();
        requestBody.put("availability", true);

        Exception exception = assertThrows(RoomNotFoundException.class,
                () -> meetingRoomService.updateMeetingRoomAvailability(roomId, requestBody.get("availability")));

        assertEquals("Meeting Room not found", exception.getMessage());

        verify(meetingRoomRepo, times(1)).findById(roomId);
        verify(meetingRoomRepo, never()).save(any());
    }

    @Test
    void updateMeetingRoomAvailability_givenRoomAlreadyInDesiredState_doesNotChangeAvailability() {
        int roomId = 1;
        OfficeModel office8 = new OfficeModel(); // Mock OfficeModel
        office8.setId(108);
        MeetingRoomModel room = new MeetingRoomModel("Conference A", "Floor 2", office8);
        room.setRoomId(roomId);
        room.setAvailable(true);

        when(meetingRoomRepo.findById(roomId)).thenReturn(Optional.of(room));

        Map<String, Boolean> requestBody = new HashMap<>();
        requestBody.put("availability", true);

        meetingRoomService.updateMeetingRoomAvailability(roomId, requestBody.get("availability"));

        assertTrue(room.isAvailable());

        verify(meetingRoomRepo, times(1)).findById(roomId);
        verify(meetingRoomRepo, times(0)).save(any()); // Ensure save() is never called
    }



}
