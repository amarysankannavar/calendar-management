package com.example.CalendarManagement.service;

import com.example.CalendarManagement.Exception.DataStorageException;
import com.example.CalendarManagement.model.OfficeModel;
import com.example.CalendarManagement.repository.OfficeRepo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class OfficeServiceTest {

    @Mock
    private OfficeRepo officeRepo;

    @InjectMocks
    private OfficeService officeService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void findOfficeByName_givenExistingOffice_returnsOffice() {
        // Arrange
        String officeName = "Capillary";
        OfficeModel office = new OfficeModel(officeName, "Bengaluru");

        when(officeRepo.findByName(officeName)).thenReturn(Optional.of(office));

        // Act
        OfficeModel result = officeService.findOfficeByName(officeName);

        // Assert
        assertNotNull(result);
        assertEquals(officeName, result.getName());
        verify(officeRepo, times(1)).findByName(officeName);
    }

    @Test
    void findOfficeByName_givenNonExistingOffice_throwsException() {
        // Arrange
        String officeName = "NonExistent";

        when(officeRepo.findByName(officeName)).thenReturn(Optional.empty());

        // Act & Assert
        Exception exception = assertThrows(RuntimeException.class, () -> officeService.findOfficeByName(officeName));
        assertEquals("Office not found with name: " + officeName, exception.getMessage());
        verify(officeRepo, times(1)).findByName(officeName);
    }

    @Test
    void createOffice_givenValidDetails_savesAndReturnsOffice() {
        // Arrange
        String name = "Capillary";
        String location = "Bengaluru";
        OfficeModel office = new OfficeModel(name, location);

        when(officeRepo.save(any(OfficeModel.class))).thenReturn(office);

        // Act
        OfficeModel result = officeService.createOffice(name, location);

        // Assert
        assertNotNull(result);
        assertEquals(name, result.getName());
        assertEquals(location, result.getOfficeLoc());
        verify(officeRepo, times(1)).save(any(OfficeModel.class));
    }

    @Test
    void createOffice_whenExceptionOccurs_throwsDataStorageException() {
        // Arrange
        when(officeRepo.save(any(OfficeModel.class))).thenThrow(new RuntimeException("Database error"));

        // Act & Assert
        Exception exception = assertThrows(DataStorageException.class, () -> officeService.createOffice("Capillary", "Bengaluru"));
        assertEquals("Failed to add the Office Room.", exception.getMessage());

        verify(officeRepo, times(1)).save(any(OfficeModel.class));
    }

    @Test
    void getAllOffices_whenOfficesExist_returnsOfficeList() {
        // Arrange
        List<OfficeModel> offices = Arrays.asList(
                new OfficeModel("Capillary", "Bengaluru"),
                new OfficeModel("Google", "Hyderabad")
        );

        when(officeRepo.findAll()).thenReturn(offices);

        // Act
        List<OfficeModel> result = officeService.getAllOffices();

        // Assert
        assertEquals(2, result.size());
        assertEquals("Capillary", result.get(0).getName());
        assertEquals("Google", result.get(1).getName());
        verify(officeRepo, times(1)).findAll();
    }
}
