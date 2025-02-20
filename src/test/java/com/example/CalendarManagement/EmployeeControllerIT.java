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
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = CalendarManagementApplication.class)
@AutoConfigureMockMvc
//@Transactional
@ActiveProfiles("test")  // Use test profile
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
//        officeRepo.deleteAll();
//        employeeRepo.deleteAll();

        nyOffice = officeRepo.save(new OfficeModel("NY Office", "New York"));
        laOffice = officeRepo.save(new OfficeModel("LA Office", "Los Angeles"));

        employeeRepo.save(new EmployeeModel("Amar", "amar@example.com", nyOffice, true));
        employeeRepo.save(new EmployeeModel("Amarys", "amarys@example.com", laOffice, true));
    }


    @Test
    void getAllEmployees_returnsEmployeesList() throws Exception {

        mockMvc.perform(get("/employees"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data", hasSize(2)))  // Expecting 2 employees
                .andExpect(jsonPath("$.data[0].name").value("Amar"))
                .andExpect(jsonPath("$.data[1].name").value("Amarys"));
    }

    @Test
    void getEmployee_givenEmployeeId_returnsEmployee() throws Exception {
        // Given: Save an employee
        EmployeeModel employee = employeeRepo.save(new EmployeeModel("Ajay", "ajay@example.com", nyOffice, true));

        // When: Perform GET request
        mockMvc.perform(get("/employees/" + employee.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.employeeId").value(employee.getId()))  // Check the employee ID under the "data" field
                .andExpect(jsonPath("$.data.name").value("Ajay"))  // Check the employee name under the "data" field
                .andExpect(jsonPath("$.data.workEmail").value("ajay@example.com"))  // Check the work email under the "data" field
                .andExpect(jsonPath("$.data.officeId").value(employee.getOffice().getId()))  // Check office ID under the "data" field
                .andExpect(jsonPath("$.data.active").value(true));  // Check if the employee is active under the "data" field
    }


    @Test
    void addEmployee_givenEmployeeDetails_createsNewEmployee() throws Exception {
        // Given: Create and save an office first
        OfficeModel office = new OfficeModel("Main Office", "New York");
        office.setId(5);
        office = officeRepo.saveAndFlush(office);  // Ensure office is saved

        // Given: Prepare employee details
        EmployeeDTO employeeDTO = new EmployeeDTO(1234, "amar", "amae@sdd", office.getId(), true);

        // When: Perform POST request to add employee
        mockMvc.perform(post("/employees")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(employeeDTO)))
                .andDo(print()) // Log the response body to see what is returned
                .andExpect(status().isCreated())  // Expect 201 status code
                .andExpect(jsonPath("$.message").value("Employee added successfully"))  // Expect success message
                .andExpect(jsonPath("$.code").value(201));  // Expect status code to be 201
    }





    @Test
    void deleteEmployee_givenExistingEmployeeId_removesEmployee() throws Exception {
        EmployeeModel employee = employeeRepo.save(new EmployeeModel("John", "john@example.com", nyOffice, true));

        mockMvc.perform(delete("/employees/" + employee.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Employee deactivated successfully"));
    }

    @Test
    void deleteEmployee_givenNonExistEmployeeId_throwsNotFoundException() throws Exception {
        mockMvc.perform(delete("/employees/9999")) // Non-existent ID
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value(404))
                .andExpect(jsonPath("$.message").value("Employee not found"));
    }
}
