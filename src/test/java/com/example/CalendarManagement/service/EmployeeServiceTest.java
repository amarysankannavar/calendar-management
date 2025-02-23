package com.example.CalendarManagement.service;

import com.example.CalendarManagement.DTO.EmployeeDTO;
import com.example.CalendarManagement.Exception.DataStorageException;
import com.example.CalendarManagement.Exception.EmployeeNotFoundException;
import com.example.CalendarManagement.model.EmployeeModel;
import com.example.CalendarManagement.model.OfficeModel;
import com.example.CalendarManagement.repository.EmployeeRepo;
import com.example.CalendarManagement.repository.MeetingStatusRepo;
import com.example.CalendarManagement.repository.OfficeRepo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class EmployeeServiceTest {

    @Mock
    private EmployeeRepo employeeRepo;

    @Mock
    private OfficeRepo officeRepo;

    @Mock
    private MeetingStatusRepo meetingStatusRepo;

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
        OfficeModel office = new OfficeModel();
        office.setId(officeId);

        when(officeRepo.findById(officeId)).thenReturn(Optional.of(office));
        when(employeeRepo.findByWorkEmail(empDTO.getWorkEmail())).thenReturn(Optional.empty());

        assertDoesNotThrow(() -> employeeService.addEmployee(empDTO));
        verify(employeeRepo, times(1)).save(any(EmployeeModel.class));
    }

    @Test
    void addEmployee_givenNonExistentOffice_throwsException() {
        EmployeeDTO empDTO = new EmployeeDTO(1, "John", "john@example.com", 99, true);
        when(officeRepo.findById(99)).thenReturn(Optional.empty());

        Exception exception = assertThrows(IllegalArgumentException.class, () -> employeeService.addEmployee(empDTO));
        assertEquals("Office not found", exception.getMessage());
    }

    @Test
    void deleteEmployee_givenEmployeeId_deactivatesEmployee() {
        int employeeId = 1;
        EmployeeModel employee = new EmployeeModel();
        employee.setId(employeeId);
        employee.setActive(true);

        when(employeeRepo.findById(employeeId)).thenReturn(Optional.of(employee));

        employeeService.deleteEmployee(employeeId);

        assertFalse(employee.isActive());
        verify(employeeRepo, times(1)).save(employee);
    }

    @Test
    void deleteEmployee_givenNonExistingEmployeeId_throwsNotFoundException() {
        int employeeId = 2;
        when(employeeRepo.findById(employeeId)).thenReturn(Optional.empty());

        Exception exception = assertThrows(EmployeeNotFoundException.class, () -> employeeService.deleteEmployee(employeeId));
        assertEquals("Employee not found", exception.getMessage());
    }

    @Test
    void getEmployees_returnsActiveEmployees() {
        OfficeModel office = new OfficeModel();
        office.setId(1);
        EmployeeModel employee1 = new EmployeeModel("Amar", "amar@example.com", office, true);
        EmployeeModel employee2 = new EmployeeModel("Sam", "sam@example.com", office, true);
        when(employeeRepo.findActiveEmployees()).thenReturn(Arrays.asList(employee1, employee2));

        List<EmployeeDTO> result = employeeService.getEmployees();

        assertThat(result)
                .hasSize(2)
                .extracting(EmployeeDTO::getName, EmployeeDTO::getWorkEmail, EmployeeDTO::getOfficeId, EmployeeDTO::isActive)
                .containsExactlyInAnyOrder(
                        tuple("Amar", "amar@example.com", 1, true),
                        tuple("Sam", "sam@example.com", 1, true)
                );
    }

    @Test
    void getEmployeeById_givenValidId_returnsEmployee() {
        int employeeId = 1;
        OfficeModel office = new OfficeModel();
        office.setId(1);
        EmployeeModel employee = new EmployeeModel("John Doe", "john@example.com", office, true);
        employee.setId(employeeId);

        when(employeeRepo.findById(employeeId)).thenReturn(Optional.of(employee));

        EmployeeDTO result = employeeService.getEmployeeById(employeeId);

        assertThat(result).isNotNull();
        assertEquals(employeeId, result.getEmployeeId());
        assertEquals("John Doe", result.getName());
    }

    @Test
    void getEmployeeById_givenNonExistentId_throwsException() {
        when(employeeRepo.findById(99)).thenReturn(Optional.empty());

        Exception exception = assertThrows(IllegalArgumentException.class, () -> employeeService.getEmployeeById(99));
        assertEquals("Employee not found", exception.getMessage());
    }
}
