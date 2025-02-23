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

import javax.validation.*;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class MeetingRoomServiceTest {

    @Mock
    private MeetingRoomRepo meetingRoomRepo;

    @Mock
    private OfficeService officeService;

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
        OfficeModel office = new OfficeModel();
        office.setId(101);
        MeetingRoomModel roomModel = new MeetingRoomModel(roomDTO.getRoomName(), roomDTO.getRoomLocation(), office);

        when(meetingRoomRepo.existsById(roomDTO.getRoomId())).thenReturn(false);
        when(officeRepo.findById(roomDTO.getOfficeId())).thenReturn(Optional.of(office));

        assertDoesNotThrow(() -> meetingRoomService.addMeetingRoom(roomDTO));

        verify(meetingRoomRepo, times(1)).save(any(MeetingRoomModel.class));
    }


    @Test
    void addMeetingRoom_givenEmptyRoomName_throwsValidationError() {
        int officeId = 1;

        when(officeRepo.findById(officeId)).thenReturn(Optional.of(new OfficeModel()));

        MeetingRoomDTO roomDTO = new MeetingRoomDTO(1, "", "Floor 2", officeId, true);

        // Manually trigger validation
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();
        Set<ConstraintViolation<MeetingRoomDTO>> violations = validator.validate(roomDTO);

        assertFalse(violations.isEmpty());
        assertEquals("Room Name can not be empty.", violations.iterator().next().getMessage());

        verify(meetingRoomRepo, never()).save(any());
    }




    @Test
    void deleteMeetingRoom_givenExistingRoomId_deletesRoom() {
        int roomId = 1;
        OfficeModel office = new OfficeModel();
        office.setId(101);
        MeetingRoomModel room = new MeetingRoomModel("Conference A", "Floor 2", office);
        room.setRoomId(roomId);

        when(meetingRoomRepo.findById(roomId)).thenReturn(Optional.of(room));
        meetingRoomService.deleteMeetingRoom(roomId);
        verify(meetingRoomRepo, times(1)).delete(room);
    }

    @Test
    void deleteMeetingRoom_givenNonExistingRoomId_throwsNotFoundException() {
        int roomId = 2;
        when(meetingRoomRepo.findById(roomId)).thenReturn(Optional.empty());
        Exception exception = assertThrows(RoomNotFoundException.class, () -> meetingRoomService.deleteMeetingRoom(roomId));
        assertEquals("Meeting Room not found", exception.getMessage());
        verify(meetingRoomRepo, never()).delete(any());
    }

    @Test
    void getMeetingRooms_whenRoomsExist_returnRoomList() {
        OfficeModel office = new OfficeModel();
        office.setId(103);
        MeetingRoomModel room1 = new MeetingRoomModel("Conference A", "Floor 2", office);
        MeetingRoomModel room2 = new MeetingRoomModel("Conference B", "Floor 3", office);
        List<MeetingRoomModel> roomModels = Arrays.asList(room1, room2);

        when(meetingRoomRepo.findAll()).thenReturn(roomModels);
        List<MeetingRoomDTO> result = meetingRoomService.getMeetingRooms();

        assertThat(result).hasSize(2)
                .extracting(MeetingRoomDTO::getRoomName, MeetingRoomDTO::getRoomLocation, MeetingRoomDTO::getOfficeId)
                .containsExactlyInAnyOrder(
                        tuple("Conference A", "Floor 2", office.getId()),
                        tuple("Conference B", "Floor 3", office.getId())
                );

        verify(meetingRoomRepo, times(1)).findAll();
    }

    @Test
    void getMeetingRoomById_givenExistingRoomId_returnsRoomInfo() {
        int roomId = 1;
        OfficeModel office = new OfficeModel();
        office.setId(106);
        MeetingRoomModel room = new MeetingRoomModel("Conference A", "Floor 2", office);
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
}
