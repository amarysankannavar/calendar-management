package com.example.CalendarManagement;
import com.example.CalendarManagement.DTO.ApiResponse;
import com.example.CalendarManagement.DTO.EmployeeDTO;
import com.example.CalendarManagement.model.EmployeeModel;
import com.example.CalendarManagement.model.MeetingModel;
import com.example.CalendarManagement.model.OfficeModel;
import com.example.CalendarManagement.repository.EmployeeRepo;
import com.example.CalendarManagement.repository.OfficeRepo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.TestPropertySource;
import org.springframework.web.client.HttpClientErrorException;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(locations = "classpath:application-test.properties")
public class EmployeeControllerIntegrationTest {

    @Autowired
    private TestRestTemplate restTemplate;

    private EmployeeDTO testEmployee;

    @Autowired
    private EmployeeRepo employeeRepo;

    @Autowired
    private OfficeRepo officeRepo;

    @BeforeEach
    void setUp() {
        OfficeModel officeModel = new OfficeModel("amazon","bengalur");
        officeRepo.save(officeModel);
        testEmployee = new EmployeeDTO(0, "Amarys", "amarys@example.com", officeModel.getId(), true);
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
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
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
    void testGetEmployeeById() {
        ResponseEntity<ApiResponse> response = restTemplate.getForEntity("/employees/1", ApiResponse.class);
        assertThat(response.getStatusCode()).isIn(HttpStatus.OK, HttpStatus.NOT_FOUND);
    }

    @Test
    void testGetEmployeeByIdNotFound() {
        ResponseEntity<ApiResponse> response = restTemplate.getForEntity("/employees/9999", ApiResponse.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void testDeleteEmployee() {
        OfficeModel officeModel = new OfficeModel("googli","bengaluru");
        officeRepo.save(officeModel);
        EmployeeModel employeeModel = new EmployeeModel("amar","amrsankannavar@gmail.com",officeModel,true);
        employeeRepo.save(employeeModel);
        int empId = employeeModel.getId();
        ResponseEntity<ApiResponse> response = restTemplate.exchange("/employees/" + empId, HttpMethod.DELETE, null, ApiResponse.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    void testDeleteEmployeeNotFound() {
        ResponseEntity<ApiResponse> response = restTemplate.exchange("/employees/9999", HttpMethod.DELETE, null, ApiResponse.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void getMeetings_withoutFilters_success() {
        String url = "/employees/getMeetings/" + testEmployee.getEmployeeId() +
                "?fromDate=2025-01-01&toDate=2025-12-10";

        ResponseEntity<ApiResponse> response = restTemplate.exchange(
                url, HttpMethod.GET, null, new ParameterizedTypeReference<ApiResponse>() {}
        );

        assertNotNull(response);
        assertThat(response.getStatusCode()).isIn(HttpStatus.OK, HttpStatus.NOT_FOUND);

        if (response.getStatusCode() == HttpStatus.OK) {
            assertNotNull(response.getBody());
            assertEquals("Meetings of employee fetched successfully", response.getBody().getMessage());
        }
    }




}
