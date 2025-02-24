package com.example.CalendarManagement.service;

import com.example.CalendarManagement.DTO.EmployeeDTO;
import com.example.CalendarManagement.Exception.DataStorageException;
import com.example.CalendarManagement.Exception.DuplicateEmailException;
import com.example.CalendarManagement.Exception.EmployeeNotFoundException;
import com.example.CalendarManagement.Exception.OfficeNotFoundException;
import com.example.CalendarManagement.model.EmployeeModel;
import com.example.CalendarManagement.model.OfficeModel;  // Imported OfficeModel
import com.example.CalendarManagement.repository.EmployeeRepo;
import com.example.CalendarManagement.repository.MeetingRepo;
import com.example.CalendarManagement.repository.MeetingStatusRepo;
import com.example.CalendarManagement.repository.OfficeRepo;  // Imported OfficeRepo
import net.bytebuddy.implementation.bytecode.Throw;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class EmployeeService {
    @Autowired
    private EmployeeRepo employeeRepo;

    @Autowired
    private OfficeRepo officeRepo;

    @Autowired
    private MeetingStatusRepo meetingStatusRepo;

    private final Logger logger = LoggerFactory.getLogger(EmployeeService.class);

    // Fetch all employees and return as EmployeeDTO list
    public List<EmployeeDTO> getEmployees() {
        MDC.put("requestId", UUID.randomUUID().toString());
        logger.info("Fetching all employees, requestId: {}", MDC.get("requestId"));

        List<EmployeeDTO> employees= employeeRepo.findActiveEmployees().stream()
                .map(emp -> new EmployeeDTO(emp.getId(), emp.getName(), emp.getWorkEmail(),
                        emp.getOffice().getId(), emp.isActive()))  // Changed to reflect office name
                .collect(Collectors.toList());
        logger.info("Fetched {} employees, requestId: {}", employees.size(), MDC.get("requestId"));
        MDC.clear();
        return employees;
    }

    // Fetch employee by ID
    public EmployeeDTO getEmployeeById(int employeeId) {
        EmployeeModel employee = employeeRepo.findById(employeeId)
                .orElseThrow(() -> new EmployeeNotFoundException("Employee not found"));

        return new EmployeeDTO(employee.getId(), employee.getName(), employee.getWorkEmail(),
                employee.getOffice().getId(), employee.isActive());
    }



    // Add new employee with validation
    public void addEmployee(EmployeeDTO empDTO) {

        MDC.put("requestId", UUID.randomUUID().toString());

        // Fetch OfficeModel based on officeId
        OfficeModel office = officeRepo.findById(empDTO.getOfficeId())
                .orElseThrow(() -> new OfficeNotFoundException("Office not found"));

        try {
            // Create and save new Employee
            logger.info("Employee added successfully - Name: {}, requestId: {}", empDTO.getName(), MDC.get("requestId"));
            EmployeeModel emp = new EmployeeModel(empDTO.getName(), empDTO.getWorkEmail(), office, empDTO.isActive());
            employeeRepo.save(emp);
        } catch (DataIntegrityViolationException e) {
            throw e;
        } catch (Exception e) {
            throw new DataStorageException("Failed to add employee.");
        }finally {
            MDC.clear(); // Clear MDC after request is processed
        }




    }

    // Delete (deactivate) employee
    public void deleteEmployee(int employeeId) {
        EmployeeModel employee = employeeRepo.findById(employeeId)
                .orElseThrow(() -> new EmployeeNotFoundException("Employee not found"));

      try {
          employee.setActive(false);
          employeeRepo.save(employee);
      } catch (Exception e) {
          throw new DataStorageException("Failed to add delete employee.");
      }
    }

    public List<Object[]> meetingsOfEmployee(int employeeId, LocalDate fromDate, LocalDate toDate){
        MDC.put("requestId", UUID.randomUUID().toString());
        logger.info("Fetching meetings for Employee ID: {} from {} to {}, requestId: {}", employeeId, fromDate, toDate, MDC.get("requestId"));

        List<Object[]> meetings =  meetingStatusRepo.findMeetingDetailsByEmployeeIdAndDateRange(employeeId,fromDate,toDate);
        logger.info("Meetings fetched: {}, requestId: {}", meetings.size(), MDC.get("requestId"));
        MDC.clear();
        return meetings;
    }

}
