package com.example.CalendarManagement;
import com.example.CalendarManagement.DTO.ApiResponse;
import com.example.CalendarManagement.DTO.EmployeeDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class EmployeeControllerIntegrationTest {

    @Autowired
    private TestRestTemplate restTemplate;

    private EmployeeDTO testEmployee;

    @BeforeEach
    void setUp() {
        testEmployee = new EmployeeDTO(0, "John Doe", "john.doe@example.com", 4, true);
    }

    @Test
    void getEmployees_returndsEmployeeDetails() {
        ResponseEntity<ApiResponse> response = restTemplate.getForEntity("/employees", ApiResponse.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
    }

    @Test
    void addEmployee_givenEmployeeDetails_createEmployee() {
        HttpEntity<EmployeeDTO> request = new HttpEntity<>(testEmployee);
        ResponseEntity<ApiResponse> response = restTemplate.postForEntity("/employees", request, ApiResponse.class);
        assertThat(response.getStatusCode()).isIn(HttpStatus.CREATED,HttpStatus.BAD_REQUEST);
    }

    @Test
    void adEmployee_givenEmptyName_throwsException() {
        EmployeeDTO invalidEmployee = new EmployeeDTO(0, "", "", 4, true);
        HttpEntity<EmployeeDTO> request = new HttpEntity<>(invalidEmployee);
        ResponseEntity<ApiResponse> response = restTemplate.postForEntity("/employees", request, ApiResponse.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    void adEmployee_givenInvalidEmailFormat_throwsException() {
        EmployeeDTO invalidEmployee = new EmployeeDTO(0, "amar", "amarr", 4, true);
        HttpEntity<EmployeeDTO> request = new HttpEntity<>(invalidEmployee);
        ResponseEntity<ApiResponse> response = restTemplate.postForEntity("/employees", request, ApiResponse.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    void getEmployeeById_givenEmployeeId_returnsEmployeeDetails() {
        ResponseEntity<ApiResponse> response = restTemplate.getForEntity("/employees/47", ApiResponse.class);
        assertThat(response.getStatusCode()).isIn(HttpStatus.OK);
    }

    @Test
    void getEmployeeById_givenNonExistingEmployeeId_throwsEmployeeNotFoundException() {
        ResponseEntity<ApiResponse> response = restTemplate.getForEntity("/employees/9999", ApiResponse.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void deleteEmployee_givenEmployeeId_softDeleteEmployee() {
        ResponseEntity<ApiResponse> response = restTemplate.exchange("/employees/47", HttpMethod.DELETE, null, ApiResponse.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    void deleteEmployee_givenNonExistEmployeeId_throwsEmployeeNotFoundException() {
        ResponseEntity<ApiResponse> response = restTemplate.exchange("/employees/9999", HttpMethod.DELETE, null, ApiResponse.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }
}
