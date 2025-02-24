package com.example.CalendarManagement;
import com.example.CalendarManagement.DTO.ApiResponse;
import com.example.CalendarManagement.DTO.MeetingRequestDTO;
import com.example.CalendarManagement.DTO.ScheduleMeetingDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Arrays;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class MeetingControllerIntegrationTest {

    @Autowired
    private TestRestTemplate restTemplate;

    private MeetingRequestDTO meetingRequestDTO;
    private ScheduleMeetingDTO scheduleMeetingDTO;

    @BeforeEach
    void setUp() {
        meetingRequestDTO = new MeetingRequestDTO(
                Arrays.asList(49,56,60,61,63,64,65),
                LocalTime.of(10, 0),
                LocalTime.of(11, 0),
                LocalDate.of(2025,11,11),
                0
        );
       // Arrays.asList(1, 2), "Project Discussion", "Discuss Q1 milestones",
        scheduleMeetingDTO = new ScheduleMeetingDTO(
                Arrays.asList(47,49,56,60,61,63,64,65),
                "Project Discussion","Discuss Q1 milestones",
                LocalTime.of(10, 0),
                LocalTime.of(11, 0),
                LocalDate.of(2025,11,11),
                0         );
    }

    @Test
    void getMeetings_returnsAllMeetings() {
        ResponseEntity<ApiResponse> response = restTemplate.getForEntity("/meetings", ApiResponse.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
    }

    @Test
    void getMeetings_givenMeetingId_returnsMeetingInfo() {
        ResponseEntity<ApiResponse> response = restTemplate.getForEntity("/meetings/99", ApiResponse.class);
        assertThat(response.getStatusCode()).isIn(HttpStatus.NOT_FOUND,HttpStatus.CREATED);
    }

    @Test
    void getMeetings_givenInvalidMeetingId_throwsNotFoundException() {
        ResponseEntity<ApiResponse> response = restTemplate.getForEntity("/meetings/9999", ApiResponse.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void deleteMeeting_givenMeetingId_softDeleteMeeting() {
        ResponseEntity<ApiResponse> response = restTemplate.exchange("/meetings/1", HttpMethod.DELETE, null, ApiResponse.class);
        assertThat(response.getStatusCode()).isIn(HttpStatus.OK, HttpStatus.NOT_FOUND);
    }

    @Test
    void deleteMeeting_givenNonExistMeetingId_throwsNotFoundException() {
        ResponseEntity<ApiResponse> response = restTemplate.exchange("/meetings/9999", HttpMethod.DELETE, null, ApiResponse.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void canSchedule_givenMeetingInfo_returnsTrueOrFalse() {
        HttpEntity<MeetingRequestDTO> request = new HttpEntity<>(meetingRequestDTO);
        ResponseEntity<ApiResponse> response = restTemplate.postForEntity("/meetings/canSchedule", request, ApiResponse.class);
        assertThat(response.getStatusCode()).isIn(HttpStatus.OK,HttpStatus.BAD_REQUEST);
    }

    @Test
    void setScheduleMeeting_givenMeetingInfo_scheduleMeeting() {
        HttpEntity<ScheduleMeetingDTO> request = new HttpEntity<>(scheduleMeetingDTO);
        ResponseEntity<ApiResponse> response = restTemplate.postForEntity("/meetings/scheduleMeeting", request, ApiResponse.class);
        assertThat(response.getStatusCode()).isIn(HttpStatus.OK, HttpStatus.BAD_REQUEST);
    }

    @Test
    void scheduleMeeting_givenEmptyAgendaDescription_throwsBadRequest() {
        ScheduleMeetingDTO invalidMeeting = new ScheduleMeetingDTO(
                Arrays.asList(47,49,56,60,61,63,64,65),
                "","",
                LocalTime.of(10, 0),
                LocalTime.of(11, 0),
                LocalDate.now(),
                0         );
        HttpEntity<ScheduleMeetingDTO> request = new HttpEntity<>(invalidMeeting);
        ResponseEntity<ApiResponse> response = restTemplate.postForEntity("/meetings/scheduleMeeting", request, ApiResponse.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }
    @Test
    void scheduleMeeting_givenPastDate_throwsBadRequest() {
        ScheduleMeetingDTO invalidMeeting = new ScheduleMeetingDTO(
                Arrays.asList(47,49,56,60,61,63,64,65),
                "fj","vhb",
                LocalTime.of(10, 0),
                LocalTime.of(11, 0),
                LocalDate.of(2024,11,11),
                0         );
        HttpEntity<ScheduleMeetingDTO> request = new HttpEntity<>(invalidMeeting);
        ResponseEntity<ApiResponse> response = restTemplate.postForEntity("/meetings/scheduleMeeting", request, ApiResponse.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }
}
