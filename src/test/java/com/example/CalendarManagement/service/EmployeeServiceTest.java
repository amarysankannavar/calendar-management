package com.example.CalendarManagement.service;

import com.example.CalendarManagement.DTO.EmployeeDTO;
import com.example.CalendarManagement.Exception.DuplicateEmailException;
import com.example.CalendarManagement.Exception.EmployeeNotFoundException;
import com.example.CalendarManagement.model.EmployeeModel;
import com.example.CalendarManagement.model.OfficeModel;
import com.example.CalendarManagement.repository.EmployeeRepo;
import com.example.CalendarManagement.repository.OfficeRepo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

class EmployeeServiceTest {

    @Mock
    private EmployeeRepo employeeRepo;

    @Mock
    private OfficeRepo officeRepo; // Mock the OfficeRepo to handle office-related operations

    @InjectMocks
    private EmployeeService employeeService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void addEmployee_givenValidEmployeeDetails_returnsSuccess() {
        int officeId = 1;
        EmployeeDTO empDTO = new EmployeeDTO(1, "Amar", "amar@capillary.com", officeId, true);
        OfficeModel office = new OfficeModel();  // Create OfficeModel mock
        office.setId(officeId);

        EmployeeModel empModel = new EmployeeModel(empDTO.getName(), empDTO.getWorkEmail(), office, empDTO.isActive());

        when(employeeRepo.existsById(empDTO.getEmployeeId())).thenReturn(false);
        when(employeeRepo.findByWorkEmail(empDTO.getWorkEmail())).thenReturn(Optional.empty());
        when(officeRepo.findById(officeId)).thenReturn(Optional.of(office)); // Mock office data

        assertDoesNotThrow(() -> employeeService.addEmployee(empDTO));

        verify(employeeRepo, times(1)).save(any(EmployeeModel.class));
    }

    @Test
    void addEmployee_givenDuplicateEmployeeId_throwsException() {
        EmployeeDTO empDTO = new EmployeeDTO(1, "New", "amar@example.com", 1, true);

        when(employeeRepo.existsById(empDTO.getEmployeeId())).thenReturn(true);

        Exception exception = assertThrows(IllegalArgumentException.class, () -> employeeService.addEmployee(empDTO));
        assertEquals("Employee ID already exists.", exception.getMessage());

        verify(employeeRepo, never()).save(any());
    }

    @Test
    void addEmployee_givenInvalidEmail_throwsValidationError() {
        EmployeeDTO empDTO = new EmployeeDTO(1, "Sham", "invalid-email", 1, true);

        when(employeeRepo.existsById(empDTO.getEmployeeId())).thenReturn(false);
        when(employeeRepo.findByWorkEmail(empDTO.getWorkEmail())).thenReturn(Optional.empty());

        Exception exception = assertThrows(IllegalArgumentException.class, () -> employeeService.addEmployee(empDTO));
        assertEquals("Invalid email format.", exception.getMessage());

        verify(employeeRepo, never()).save(any());
    }

    @Test
    void addEmployee_givenDuplicateEmail_throwsException() {
        EmployeeDTO empDTO = new EmployeeDTO(1, "Amar", "amar@capillary.com", 1, true);
        EmployeeModel existingEmployee = new EmployeeModel(empDTO.getName(), empDTO.getWorkEmail(), new OfficeModel(), empDTO.isActive());

        when(employeeRepo.existsById(empDTO.getEmployeeId())).thenReturn(false);
        when(employeeRepo.findByWorkEmail(empDTO.getWorkEmail())).thenReturn(Optional.of(existingEmployee));

        Exception exception = assertThrows(DuplicateEmailException.class, () -> employeeService.addEmployee(empDTO));
        assertEquals("Email already in use.", exception.getMessage());

        verify(employeeRepo, never()).save(any());
    }

    @Test
    void addEmployee_givenEmptyName_throwsValidationError() {
        EmployeeDTO empDTO = new EmployeeDTO(1, "", "amar@example.com", 1, true);

        when(employeeRepo.existsById(empDTO.getEmployeeId())).thenReturn(false);
        when(employeeRepo.findByWorkEmail(empDTO.getWorkEmail())).thenReturn(Optional.empty());

        Exception exception = assertThrows(IllegalArgumentException.class, () -> employeeService.addEmployee(empDTO));
        assertEquals("Employee name cannot be empty.", exception.getMessage());

        verify(employeeRepo, never()).save(any());
    }

    @Test
    void removeEmployee_givenEmployeeId_deletesEmployee() {
        int employeeId = 1;
        EmployeeModel employee = new EmployeeModel();
        employee.setId(employeeId);
        employee.setName("John Doe");

        when(employeeRepo.findById(employeeId)).thenReturn(Optional.of(employee));

        employeeService.deleteEmployee(employeeId);

        assertFalse(employee.isActive());

        verify(employeeRepo, times(1)).save(employee);  // Ensure delete was called once
    }


    @Test
    void removeEmployee_givenNonExistingEmployeeId_throwsNotFoundException(){
        int employeeId=2;

        when(employeeRepo.findById(employeeId)).thenReturn(Optional.empty());

        Exception exception = assertThrows(EmployeeNotFoundException.class,()-> employeeService.deleteEmployee(employeeId));
        assertEquals("Employee not found",exception.getMessage());
        verify(employeeRepo, times(1)).findById(employeeId);
        verify(employeeRepo, never()).save(any(EmployeeModel.class));
    }

    @Test
    void getEmployees_givenValidEmployeeId_returnsEmployeeInfo() {
        // Given
        int employeeId = 1;
        EmployeeModel employee = new EmployeeModel();
        employee.setId(employeeId);
        employee.setName("John Doe");
        employee.setActive(true);
        employee.setOffice(new OfficeModel());  // Mock office info

        when(employeeRepo.findById(employeeId)).thenReturn(Optional.of(employee));

        // When
        EmployeeDTO result = employeeService.getEmployeeById(employeeId);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getEmployeeId()).isEqualTo(employeeId);
        assertThat(result.getName()).isEqualTo("John Doe");
        assertThat(result.isActive()).isTrue();

        verify(employeeRepo, times(1)).findById(employeeId);
    }

    @Test
    void getEmployees_whenEmployeesExist_returnEmployeeList() {
        // Given
        OfficeModel office = new OfficeModel();
        office.setId(1);
        EmployeeModel employee1 = new EmployeeModel("Amar", "amar@example.com", office, true);
        EmployeeModel employee2 = new EmployeeModel("Amarys", "amarys@example.com", office, true);

        List<EmployeeModel> employeeModels = Arrays.asList(employee1, employee2);

        when(employeeRepo.findAll()).thenReturn(employeeModels);

        // When
        List<EmployeeDTO> result = employeeService.getEmployees();

        // Then
        assertThat(result)
                .hasSize(2)
                .extracting(EmployeeDTO::getName, EmployeeDTO::getWorkEmail, EmployeeDTO::getOfficeId, EmployeeDTO::isActive)
                .containsExactlyInAnyOrder(
                        tuple("Amar", "amar@example.com", 1, true),
                        tuple("Amarys", "amarys@example.com", 1, true)
                );

        verify(employeeRepo, times(1)).findAll();
    }

    @Test
    void getEmployee_givenNonExistEmployeeId_throwsNotFoundException(){
        int employeeId=23;

        when(employeeRepo.findById(employeeId)).thenReturn(Optional.empty());

        Exception exception = assertThrows(IllegalArgumentException.class, ()->employeeService.getEmployeeById(employeeId));

        assertEquals("Employee not found",exception.getMessage());

        verify(employeeRepo, times(1)).findById(employeeId);
    }
}
