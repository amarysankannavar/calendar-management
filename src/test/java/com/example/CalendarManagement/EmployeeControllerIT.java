package com.example.CalendarManagement;

import com.example.CalendarManagement.DTO.EmployeeDTO;
import com.example.CalendarManagement.model.EmployeeModel;
import com.example.CalendarManagement.model.OfficeModel;
import com.example.CalendarManagement.repository.EmployeeRepo;
import com.example.CalendarManagement.repository.OfficeRepo;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import javax.transaction.Transactional;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = CalendarManagementApplication.class)
@AutoConfigureMockMvc
@ActiveProfiles("test")
@TestPropertySource("classpath:application-test.properties")
public class EmployeeControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private EmployeeRepo employeeRepo;

    @Autowired
    private OfficeRepo officeRepo;

    private OfficeModel nyOffice;
    private OfficeModel laOffice;

    @BeforeEach
    void setUp() {
        employeeRepo.deleteAll();
        officeRepo.deleteAll();

        nyOffice = officeRepo.save(new OfficeModel("NY Office", "New York"));
        laOffice = officeRepo.save(new OfficeModel("LA Office", "Los Angeles"));

        employeeRepo.save(new EmployeeModel("Amar", "amar@example.com", nyOffice, true));
        employeeRepo.save(new EmployeeModel("Amarys", "amarys@example.com", laOffice, true));
    }


    @Test
    void getAllEmployees_returnsEmployeesList() throws Exception {
        mockMvc.perform(get("/employees"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data", hasSize(2)))
                .andExpect(jsonPath("$.data[0].name").value("Amar"))
                .andExpect(jsonPath("$.data[1].name").value("Amarys"));
    }

    @Test
    void getEmployee_givenEmployeeId_returnsEmployee() throws Exception {
        EmployeeModel employee = employeeRepo.save(new EmployeeModel("Ajay", "ajay@example.com", nyOffice, true));

        mockMvc.perform(get("/employees/" + employee.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.employeeId").value(employee.getId()))
                .andExpect(jsonPath("$.data.name").value("Ajay"))
                .andExpect(jsonPath("$.data.workEmail").value("ajay@example.com"))
                .andExpect(jsonPath("$.data.officeId").value(nyOffice.getId()))
                .andExpect(jsonPath("$.data.active").value(true));
    }

    @Test
    void addEmployee_givenEmployeeDetails_createsNewEmployee() throws Exception {
        OfficeModel office = officeRepo.saveAndFlush(new OfficeModel("Main Office", "New York"));

        EmployeeDTO employeeDTO = new EmployeeDTO(0, "amar", "amars@example.com", office.getId(), true);

        mockMvc.perform(post("/employees")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(employeeDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.message").value("Employee added successfully"))
                .andExpect(jsonPath("$.code").value(201));
    }

    @Test
    void deleteEmployee_givenExistingEmployeeId_removesEmployee() throws Exception {
        EmployeeModel employee = employeeRepo.save(new EmployeeModel("John", "john@example.com", nyOffice, true));

        mockMvc.perform(delete("/employees/" + employee.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Employee deactivated successfully"));
    }

    @Test
    void getEmployee_givenNonExistingEmployeeId_returnsNotFound() throws Exception {
        mockMvc.perform(get("/employees/9999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value(404))
                .andExpect(jsonPath("$.message").value("Employee not found"));
    }

    @Test
    void addEmployee_givenMissingFields_returnsBadRequest() throws Exception {
        EmployeeDTO employeeDTO = new EmployeeDTO(0, "", "", 0, true); // Missing name & email

        mockMvc.perform(post("/employees")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(employeeDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(400))
                .andExpect(jsonPath("$.message").value("Invalid input data"));
    }

    @Test
    void addEmployee_givenInvalidOfficeId_returnsNotFound() throws Exception {
        EmployeeDTO employeeDTO = new EmployeeDTO(0, "vikas", "vikas@example.com", 9999, true); // Non-existent office

        mockMvc.perform(post("/employees")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(employeeDTO)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value(404))
                .andExpect(jsonPath("$.message").value("Office not found"));
    }

    @Test
    void deleteEmployee_givenNonExistingEmployeeId_returnsNotFound() throws Exception {
        mockMvc.perform(delete("/employees/9999")) // Non-existent ID
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value(404))
                .andExpect(jsonPath("$.message").value("Employee not found"));
    }


}
