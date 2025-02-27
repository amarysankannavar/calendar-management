package com.example.CalendarManagement;

import com.example.CalendarManagement.DTO.ApiResponse;
import com.example.CalendarManagement.Exception.OfficeNotFoundException;
import com.example.CalendarManagement.model.OfficeModel;
import com.example.CalendarManagement.repository.OfficeRepo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.*;
import org.springframework.test.context.TestPropertySource;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(locations = "classpath:application-test.properties")
public class OfficeControllerIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private OfficeRepo officeRepo;

    private String baseUrl;

    @BeforeEach
    void setUp() {
        baseUrl = "http://localhost:" + port + "/offices";
        officeRepo.deleteAll();
        officeRepo.flush();
        officeRepo.save(new OfficeModel("Google", "Bangalore"));
    }

    @Test
    void testCreateOffice_Success() {
        OfficeModel request = new OfficeModel("Microsoft", "Hyderabad");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<OfficeModel> entity = new HttpEntity<>(request, headers);

        ResponseEntity<OfficeModel> response = restTemplate.postForEntity(baseUrl, entity, OfficeModel.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getName()).isEqualTo("Microsoft");
        assertThat(response.getBody().getOfficeLoc()).isEqualTo("Hyderabad");
    }

    @Test
    void testGetAllOffices_Success() {
        ResponseEntity<OfficeModel[]> response = restTemplate.getForEntity(baseUrl, OfficeModel[].class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotEmpty();
    }

    @Test
    void testGetOfficeByName_Success() {
        ResponseEntity<OfficeModel> response = restTemplate.getForEntity(baseUrl + "/Google", OfficeModel.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getName()).isEqualTo("Google");
        assertThat(response.getBody().getOfficeLoc()).isEqualTo("Bangalore");
    }

    @Test
    void testDeleteOffice_Success() {
        OfficeModel office = officeRepo.findByName("Google").orElseThrow(() -> new OfficeNotFoundException("Office not found"));

        ResponseEntity<String> response = restTemplate.exchange(baseUrl + "/" + office.getId(), HttpMethod.DELETE, null, String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo("Office deleted successfully");

        assertThat(officeRepo.findById(office.getId())).isEmpty();
    }


    @Test
    void testDeleteOffice_NotFound_Failure() {
        ResponseEntity<ApiResponse> response = restTemplate.exchange(baseUrl + "/9999", HttpMethod.DELETE, null, ApiResponse.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getMessage()).isEqualTo("Office not found");
    }
}
