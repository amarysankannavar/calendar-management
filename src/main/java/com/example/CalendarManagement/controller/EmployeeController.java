package com.example.CalendarManagement.controller;

import com.example.CalendarManagement.DTO.*;
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
import java.util.HashMap;
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

    @GetMapping("/getMeetings/{employeeId}")
    public ResponseEntity<ApiResponse<List<MeetingsDTO>>> getMeetingsOfEmployee(
            @PathVariable int employeeId,
            @RequestParam(required = false) String fromDate,
            @RequestParam(required = false) String toDate,
            @RequestParam(defaultValue = "false") boolean currentWeek) {

        LocalDate startDate;
        LocalDate endDate;

        if (currentWeek) {
            startDate = LocalDate.now();
            endDate = startDate.plusDays(6);
        } else if (fromDate == null && toDate == null) {
            List<MeetingsDTO> meetings = service.meetingsOfEmployee(employeeId, null, null);
            return ResponseEntity.ok(new ApiResponse<>("Meetings of employee fetched successfully", 200, meetings, null));
        } else {
            try {
                startDate = (fromDate != null) ? LocalDate.parse(fromDate) : null;
                endDate = (toDate != null) ? LocalDate.parse(toDate) : null;

                if (startDate == null || endDate == null || endDate.isBefore(startDate)) {
                    HashMap<String, String> errorDetails = new HashMap<>();
                    errorDetails.put("details", "Invalid date");
                    return ResponseEntity.ok(new ApiResponse<>("The dates are not valid.", 400, null, errorDetails));
                }
            } catch (Exception e) {
                HashMap<String, String> errorDetails = new HashMap<>();
                errorDetails.put("details", "Invalid date format. Expected format: YYYY-MM-DD");
                return ResponseEntity.ok(new ApiResponse<>("The dates are not valid.", 400, null, errorDetails));
            }
        }

        List<MeetingsDTO> meetings = service.meetingsOfEmployee(employeeId, startDate, endDate);
        return ResponseEntity.ok(new ApiResponse<>("Meetings of employee fetched successfully", 200, meetings, null));
    }



}
