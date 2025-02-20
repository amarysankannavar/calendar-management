package com.example.CalendarManagement.service;

import com.example.CalendarManagement.DTO.EmployeeDTO;
import com.example.CalendarManagement.Exception.DuplicateEmailException;
import com.example.CalendarManagement.Exception.EmployeeNotFoundException;
import com.example.CalendarManagement.model.EmployeeModel;
import com.example.CalendarManagement.model.OfficeModel;  // Imported OfficeModel
import com.example.CalendarManagement.repository.EmployeeRepo;
import com.example.CalendarManagement.repository.MeetingRepo;
import com.example.CalendarManagement.repository.MeetingStatusRepo;
import com.example.CalendarManagement.repository.OfficeRepo;  // Imported OfficeRepo
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
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
        /*return employeeRepo.findAll().stream()
                .map(emp -> new EmployeeDTO(emp.getId(), emp.getName(), emp.getWorkEmail(),
                        emp.getOffice().getId(), emp.isActive()))  // Changed to reflect office name
                .collect(Collectors.toList()); */
        return employeeRepo.findActiveEmployees().stream()
                .map(emp -> new EmployeeDTO(emp.getId(), emp.getName(), emp.getWorkEmail(),
                        emp.getOffice().getId(), emp.isActive()))  // Changed to reflect office name
                .collect(Collectors.toList());
    }

    // Fetch employee by ID
    public EmployeeDTO getEmployeeById(int employeeId) {
        EmployeeModel employee = employeeRepo.findById(employeeId)
                .orElseThrow(() -> new IllegalArgumentException("Employee not found"));

        return new EmployeeDTO(employee.getId(), employee.getName(), employee.getWorkEmail(),
                employee.getOffice().getId(), employee.isActive());
    }



    // Add new employee with validation
    public void addEmployee(EmployeeDTO empDTO) {
       /* if (empDTO.getEmployeeId() != 0 && employeeRepo.existsById(empDTO.getEmployeeId())) {
            throw new IllegalArgumentException("Employee ID already exists.");
        }

        // Validate name
        if (empDTO.getName() == null || empDTO.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Employee name cannot be empty.");
        }

        // Validate email format
        if (!empDTO.getWorkEmail().matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            throw new IllegalArgumentException("Invalid email format.");
        }   */

        // Check duplicate email
        if (employeeRepo.findByWorkEmail(empDTO.getWorkEmail()).isPresent()) {
            throw new DuplicateEmailException("Email already in use.");
        }

        // Fetch OfficeModel based on officeId
        OfficeModel office = officeRepo.findById(empDTO.getOfficeId())
                .orElseThrow(() -> new IllegalArgumentException("Office not found"));

        // Create and save new Employee
        EmployeeModel emp = new EmployeeModel(empDTO.getName(), empDTO.getWorkEmail(), office, empDTO.isActive());
        employeeRepo.save(emp);
    }

    // Delete (deactivate) employee
    public void deleteEmployee(int employeeId) {
        EmployeeModel employee = employeeRepo.findById(employeeId)
                .orElseThrow(() -> new EmployeeNotFoundException("Employee not found"));

        employee.setActive(false);  // Deactivating the employee instead of deleting
        employeeRepo.save(employee);
    }
/*
    public boolean deleteEmployee(int employeeId) {
        Optional<EmployeeModel> employee = employeeRepo.findById(employeeId);
        if (employee.isPresent()) {
            employeeRepo.delete(employee.get());
            return true;  // Employee deleted successfully
        }
        else {
            throw new EmployeeNotFoundException("Employee not found");
        }


    }
*/
    public List<Object[]> meetingsOfEmployee(int employeeId, LocalDate fromDate, LocalDate toDate){
        logger.info("inputs:"+employeeId+" "+fromDate+" "+toDate);
        List<Object[]> meetings =  meetingStatusRepo.findMeetingDetailsByEmployeeIdAndDateRange(employeeId,fromDate,toDate);
        logger.info("The meeting details of the given employees are:"+meetings);

        return meetings;
    }

}
