package com.example.CalendarManagement.controller;

import com.example.CalendarManagement.DTO.ApiResponse;
import com.example.CalendarManagement.DTO.EmployeeDTO;
import com.example.CalendarManagement.DTO.EmployeeMeetingsDTO;
import com.example.CalendarManagement.service.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@RestController
@RequestMapping("/employees")
@Validated
public class EmployeeController {

    @Autowired
    private EmployeeService service;

    @GetMapping
    public ApiResponse<List<EmployeeDTO>> getAllEmployees() {
        List<EmployeeDTO> employees = service.getEmployees();
        return new ApiResponse<>("Employees fetched successfully", 200, employees, null);
    }

    @GetMapping("/{employeeId}")
    public ResponseEntity<ApiResponse<EmployeeDTO>> getEmployeeById(@PathVariable int employeeId) {
        EmployeeDTO employee = service.getEmployeeById(employeeId);
        if (employee != null) {
            return ResponseEntity.ok(new ApiResponse<>("Employee fetched successfully", 200, employee, null));
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse<>("Employee not found", 404, null, null));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<String>> addEmployee(@Valid @RequestBody EmployeeDTO emp) {
        service.addEmployee(emp);
        return ResponseEntity.status(HttpStatus.CREATED).body(new ApiResponse<>("Employee added successfully", 201, "Success", null));
    }

    @DeleteMapping("/{employeeId}")
    public ResponseEntity<ApiResponse<String>> deleteEmployee(@PathVariable int employeeId) {
        service.deleteEmployee(employeeId);

        return ResponseEntity.ok(new ApiResponse<>("Employee deactivated successfully", 200, "Success", null));

    }

    @PostMapping("/getMeetings/{employeeId}")  // Change from GET to POST (since we use a JSON body)
    public ResponseEntity<ApiResponse<List<Object[]>>> getMeetingsOfEmployee(
            @PathVariable int employeeId,
            @RequestBody EmployeeMeetingsDTO request) {

        if (request.getFromDate() == null || request.getToDate() == null) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse<>("Missing required date parameters", 400, null, null));
        }

        List<Object[]> meetings = service.meetingsOfEmployee(employeeId, request.getFromDate(), request.getToDate());

        return ResponseEntity.ok(new ApiResponse<>("Meetings of employee fetched successfully", 200, meetings, null));
    }



}
