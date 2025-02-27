package com.example.CalendarManagement;

import com.example.CalendarManagement.DTO.ApiResponse;
import com.example.CalendarManagement.DTO.MeetingRoomDTO;
import com.example.CalendarManagement.model.MeetingRoomModel;
import com.example.CalendarManagement.model.OfficeModel;
import com.example.CalendarManagement.repository.MeetingRoomRepo;
import com.example.CalendarManagement.repository.OfficeRepo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.*;
import org.springframework.test.context.TestPropertySource;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(locations = "classpath:application-test.properties")
public class MeetingRoomControllerIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private OfficeRepo officeRepo;

    @Autowired
    private MeetingRoomRepo meetingRoomRepo;

    private OfficeModel testOffice;
    private MeetingRoomModel testMeetingRoom;
    private String baseUrl;

    @BeforeEach
    void setUp() {
        baseUrl = "http://localhost:" + port + "/meetingRooms";

        // Create a test office
        testOffice = new OfficeModel("Google", "Bangalore");
        officeRepo.save(testOffice);

        // Create a test meeting room
        testMeetingRoom = new MeetingRoomModel("Room A", "Floor 1", testOffice);
        meetingRoomRepo.save(testMeetingRoom);
    }

    /** SUCCESS CASES **/

    @Test
    void testAddMeetingRoom_Success() {
        MeetingRoomDTO request = new MeetingRoomDTO(0, "Room B", "Floor 2", testOffice.getId(), true);

        ResponseEntity<ApiResponse> response = restTemplate.postForEntity(baseUrl, request, ApiResponse.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().getMessage()).isEqualTo("Meeting Room added successfully");
    }

    @Test
    void testGetMeetingRoomById_Success() {
        ResponseEntity<MeetingRoomDTO> response = restTemplate.getForEntity(baseUrl + "/" + testMeetingRoom.getRoomId(), MeetingRoomDTO.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().getRoomName()).isEqualTo("Room A");
        assertThat(response.getBody().getRoomLocation()).isEqualTo("Floor 1");
    }

    @Test
    void testUpdateMeetingRoomAvailability_Success() {
        Map<String, Boolean> requestBody = new HashMap<>();
        requestBody.put("availability", false);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Map<String, Boolean>> requestEntity = new HttpEntity<>(requestBody, headers);

        ResponseEntity<ApiResponse> response = restTemplate.exchange(baseUrl + "/" + testMeetingRoom.getRoomId(), HttpMethod.PUT, requestEntity, ApiResponse.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().getMessage()).isEqualTo("Meeting Room availability updated successfully");
    }

    @Test
    void testDeleteMeetingRoom_Success() {
        ResponseEntity<ApiResponse> response = restTemplate.exchange(baseUrl + "/" + testMeetingRoom.getRoomId(), HttpMethod.DELETE, null, ApiResponse.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().getMessage()).isEqualTo("Meeting Room deactivated successfully");
    }

    /** FAILURE CASES **/

    @Test
    void testAddMeetingRoom_InvalidOfficeId_Failure() {
        MeetingRoomDTO request = new MeetingRoomDTO(0, "Room C", "Floor 3", 9999, true);

        ResponseEntity<ApiResponse> response = restTemplate.postForEntity(baseUrl, request, ApiResponse.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody().getMessage()).contains("Invalid input data");
    }

    @Test
    void testGetMeetingRoomById_NotFound_Failure() {
        ResponseEntity<ApiResponse> response = restTemplate.getForEntity(baseUrl + "/9999", ApiResponse.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody().getMessage()).isEqualTo("Meeting Room not found");
    }

    @Test
    void testUpdateMeetingRoomAvailability_NotFound_Failure() {
        Map<String, Boolean> requestBody = new HashMap<>();
        requestBody.put("availability", true);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Map<String, Boolean>> requestEntity = new HttpEntity<>(requestBody, headers);

        ResponseEntity<ApiResponse> response = restTemplate.exchange(baseUrl + "/9999", HttpMethod.PUT, requestEntity, ApiResponse.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody().getMessage()).isEqualTo("Meeting Room not found");
    }

    @Test
    void testDeleteMeetingRoom_NotFound_Failure() {
        ResponseEntity<ApiResponse> response = restTemplate.exchange(baseUrl + "/9999", HttpMethod.DELETE, null, ApiResponse.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody().getMessage()).isEqualTo("Meeting Room not found");
    }
}
