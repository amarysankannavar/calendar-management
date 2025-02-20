package com.example.CalendarManagement.Exception;

import com.example.CalendarManagement.DTO.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
class GlobalExceptionHandler {

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiResponse<String>> handleIllegalArgumentException(IllegalArgumentException ex) {
        ApiResponse<String> response = new ApiResponse<>(
                "Invalid input data",
                400,
                null,
                getErrorDetails(ex.getMessage())
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }


    @ExceptionHandler(DuplicateEmailException.class)
    public ResponseEntity<ApiResponse<String>> handleDuplicateEmailException(DuplicateEmailException ex) {
        ApiResponse<String> response= new ApiResponse<>(
                "Invalid input data",
                400,
                null,
                getErrorDetails(ex.getMessage())
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<String>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errorDetails = new HashMap<>();

        ex.getBindingResult().getFieldErrors().forEach(error ->
                errorDetails.put(error.getField(), error.getDefaultMessage())
        );
        ApiResponse<String> response = new ApiResponse<>(
                "Invalid input data",
                400,
                null,
                errorDetails
        );
        return ResponseEntity.badRequest().body(response);
    }

    @ExceptionHandler(EmployeeNotFoundException.class)
    public ResponseEntity<ApiResponse<String>> handleEmployeeNotFoundException(EmployeeNotFoundException ex) {
        Map<String,String> errorDetails= new HashMap<>();
        errorDetails.put("details",ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ApiResponse<>(ex.getMessage(), 404, "Error", errorDetails));
    }

    @ExceptionHandler(RoomNotFoundException.class)
    public ResponseEntity<ApiResponse<String>> handleRoomNotFoundException(RoomNotFoundException ex) {
        Map<String,String> errorDetails= new HashMap<>();
        errorDetails.put("details",ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ApiResponse<>(ex.getMessage(), 404, "Error", errorDetails));
    }

    @ExceptionHandler(EmployeesNotAvailableException.class)
    public ResponseEntity<ApiResponse<String>> handleEmployeeNotAvailableException(EmployeesNotAvailableException ex){
        Map<String, String> errorDetails = new HashMap<>();
        errorDetails.put("details", ex.getMessage()); // Additional details
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(new ApiResponse<>(ex.getMessage(), 409, "Error", errorDetails));
    }


    private Map<String, String> getErrorDetails(String message) {
        Map<String, String> errorDetails = new HashMap<>();
        errorDetails.put("details", message);
        return errorDetails;
    }
}
