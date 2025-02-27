package com.example.CalendarManagement.service;

import com.example.CalendarManagement.Exception.DataStorageException;
import com.example.CalendarManagement.Exception.OfficeNotFoundException;
import com.example.CalendarManagement.model.MeetingRoomModel;
import com.example.CalendarManagement.model.OfficeModel;
import com.example.CalendarManagement.repository.MeetingRoomRepo;
import com.example.CalendarManagement.repository.OfficeRepo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OfficeServiceTest {

    @Mock
    private OfficeRepo officeRepo;

    @Mock
    private MeetingRoomRepo meetingRoomRepo;

    @Mock
    private MeetingRoomService meetingRoomService;

    @InjectMocks
    private OfficeService officeService;

    private OfficeModel office1;
    private OfficeModel office2;

    @BeforeEach
    void setUp() {
        office1 = new OfficeModel("OfficeA", "LocationA");
        office1.setId(1);

        office2 = new OfficeModel("OfficeB", "LocationB");
        office2.setId(2);
    }

    @Test
    void getAllOffices_ShouldReturnAllOffices() {
        when(officeRepo.findAll()).thenReturn(Arrays.asList(office1, office2));

        List<OfficeModel> offices = officeService.getAllOffices();

        assertEquals(2, offices.size());
        verify(officeRepo, times(1)).findAll();
    }

    @Test
    void findOfficeByName_ShouldReturnOffice() {
        when(officeRepo.findByName("OfficeA")).thenReturn(Optional.of(office1));

        OfficeModel foundOffice = officeService.findOfficeByName("OfficeA");

        assertNotNull(foundOffice);
        assertEquals("OfficeA", foundOffice.getName());
        verify(officeRepo, times(1)).findByName("OfficeA");
    }

    @Test
    void findOfficeByName_WhenOfficeNotFound_ShouldThrowException() {
        when(officeRepo.findByName("NonExistingOffice")).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> officeService.findOfficeByName("NonExistingOffice"));
    }

    @Test
    void addOffice_ShouldSaveAndReturnOffice() {
        when(officeRepo.save(any(OfficeModel.class))).thenReturn(office1);

        OfficeModel createdOffice = officeService.createOffice("OfficeA", "LocationA");

        assertNotNull(createdOffice);
        assertEquals("OfficeA", createdOffice.getName());
        verify(officeRepo, times(1)).save(any(OfficeModel.class));
    }

    @Test
    void addOffice_WhenDataStorageException_ShouldThrowException() {
        when(officeRepo.save(any(OfficeModel.class))).thenThrow(new RuntimeException());

        assertThrows(DataStorageException.class, () -> officeService.createOffice("OfficeX", "LocationX"));
    }

    @Test
    void deleteOffice_ShouldDeleteOfficeAndDeactivateRooms() {
        when(officeRepo.findById(1)).thenReturn(Optional.of(office1));

        MeetingRoomModel room1 = new MeetingRoomModel("Room1", "Floor1", office1);
        room1.setRoomId(101);
        MeetingRoomModel room2 = new MeetingRoomModel("Room2", "Floor2", office1);
        room2.setRoomId(102);

        when(meetingRoomRepo.findByOffice(office1)).thenReturn(Arrays.asList(room1, room2));

        boolean result = officeService.deleteOffice(1);

        assertTrue(result);
        verify(meetingRoomService, times(2)).deleteMeetingRoom(anyInt());
        verify(officeRepo, times(1)).delete(office1);
    }

    @Test
    void deleteOffice_WhenOfficeNotFound_ShouldThrowException() {
        when(officeRepo.findById(999)).thenReturn(Optional.empty());

        assertThrows(OfficeNotFoundException.class, () -> officeService.deleteOffice(999));
    }
}
