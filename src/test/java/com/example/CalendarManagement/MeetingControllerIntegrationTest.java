package com.example.CalendarManagement;
import com.example.CalendarManagement.DTO.ApiResponse;
import com.example.CalendarManagement.DTO.MeetingRequestDTO;
import com.example.CalendarManagement.DTO.ScheduleMeetingDTO;
import com.example.CalendarManagement.model.EmployeeModel;
import com.example.CalendarManagement.model.MeetingRoomModel;
import com.example.CalendarManagement.model.OfficeModel;
import com.example.CalendarManagement.repository.EmployeeRepo;
import com.example.CalendarManagement.repository.MeetingRoomRepo;
import com.example.CalendarManagement.repository.OfficeRepo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.springframework.test.context.TestPropertySource;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Arrays;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(locations = "classpath:application-test.properties")

public class MeetingControllerIntegrationTest {

    @Autowired
    private TestRestTemplate restTemplate;

    private MeetingRequestDTO meetingRequestDTO;
    private ScheduleMeetingDTO scheduleMeetingDTO;

    @Autowired
    private OfficeRepo officeRepo;

    @Autowired
    private EmployeeRepo employeeRepo;

    @Autowired
    private MeetingRoomRepo meetingRoomRepo;



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
        OfficeModel officeModel =officeRepo.save( new OfficeModel("capillary","bengaluru"));
        EmployeeModel e1 = employeeRepo.save(new EmployeeModel("amar","amar11@gmail.com",officeModel,true));
        EmployeeModel e2 = employeeRepo.save(new EmployeeModel("amar","amar12@gmail.com",officeModel,true));
        EmployeeModel e3 = employeeRepo.save(new EmployeeModel("amar","amar13@gmail.com",officeModel,true));
        EmployeeModel e4 = employeeRepo.save(new EmployeeModel("amar","amar41@gmail.com",officeModel,true));
        EmployeeModel e5 = employeeRepo.save(new EmployeeModel("amar","amar15@gmail.com",officeModel,true));
        EmployeeModel e6 = employeeRepo.save(new EmployeeModel("amar","amar16@gmail.com",officeModel,true));
        MeetingRoomModel meetingRoom = meetingRoomRepo.save(new MeetingRoomModel("s22","2nd floor",officeModel));
        meetingRequestDTO = new MeetingRequestDTO(
                Arrays.asList(e1.getId(),e2.getId(),e3.getId(),e4.getId(),e5.getId(),e6.getId()),
                LocalTime.of(10, 0),
                LocalTime.of(11, 0),
                LocalDate.of(2025,11,11),
                meetingRoom.getRoomId()
        );

        HttpEntity<MeetingRequestDTO> request = new HttpEntity<>(meetingRequestDTO);
        ResponseEntity<ApiResponse> response = restTemplate.postForEntity("/meetings/canSchedule", request, ApiResponse.class);
        assertThat(response.getStatusCode()).isIn(HttpStatus.OK,HttpStatus.BAD_REQUEST);
    }

    @Test
    void setScheduleMeeting_givenMeetingInfo_scheduleMeeting() {
        OfficeModel officeModel = officeRepo.save(new OfficeModel("amazon", "bengaluru"));

        EmployeeModel e1 = employeeRepo.save(new EmployeeModel("amar", "amasfsr112@gmail.com", officeModel, true));
        EmployeeModel e2 = employeeRepo.save(new EmployeeModel("amar", "amarfsf22@gmail.com", officeModel, true));
        EmployeeModel e3 = employeeRepo.save(new EmployeeModel("amar", "asffmar32@gmail.com", officeModel, true));
        EmployeeModel e4 = employeeRepo.save(new EmployeeModel("amar", "amfsfar42@gmail.com", officeModel, true));
        EmployeeModel e5 = employeeRepo.save(new EmployeeModel("amar", "amsffar52@gmail.com", officeModel, true));
        EmployeeModel e6 = employeeRepo.save(new EmployeeModel("amar", "amasfsfr62@gmail.com", officeModel, true));

        MeetingRoomModel meetingRoom = meetingRoomRepo.save(new MeetingRoomModel("s31", "2nd floor", officeModel));

        ScheduleMeetingDTO validMeeting = new ScheduleMeetingDTO(
                Arrays.asList(e1.getId(), e2.getId(), e3.getId(), e4.getId(), e5.getId(), e6.getId()),
                "this is description", "this is agenda",
                LocalTime.of(10, 0),
                LocalTime.of(11, 0),
                LocalDate.of(2025, 11, 11),
                meetingRoom.getRoomId());

        HttpEntity<ScheduleMeetingDTO> request = new HttpEntity<>(validMeeting);
        ResponseEntity<ApiResponse> response = restTemplate.postForEntity("/meetings/scheduleMeeting", request, ApiResponse.class);

        // Assertions
        assertThat(response.getStatusCode()).isIn(HttpStatus.OK,HttpStatus.BAD_REQUEST);
        // Adjust message
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
