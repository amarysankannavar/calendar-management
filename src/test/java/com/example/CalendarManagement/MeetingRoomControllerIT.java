package com.example.CalendarManagement;

import com.example.CalendarManagement.model.MeetingRoomModel;
import com.example.CalendarManagement.model.OfficeModel;
import com.example.CalendarManagement.repository.MeetingRoomRepo;
import com.example.CalendarManagement.repository.OfficeRepo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import javax.transaction.Transactional;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@Transactional
@ActiveProfiles("test")
@TestPropertySource("classpath:application-test.properties")
public class MeetingRoomControllerIT {

    private Logger logger = LoggerFactory.getLogger(MeetingRoomControllerIT.class);

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    MeetingRoomRepo meetingRoomRepo;

    @Autowired
    OfficeRepo officeRepo; // Add this to access the OfficeModel

    @BeforeEach
    void setUp() {
        OfficeModel office1 = new OfficeModel("Capillary", "Bengaluru");
        OfficeModel office2 = new OfficeModel("Google", "Bengaluru");

        office1 = officeRepo.save(office1);
        office2 = officeRepo.save(office2);

        meetingRoomRepo.save(new MeetingRoomModel("Conference Room A", "NY Office", office1));
        meetingRoomRepo.save(new MeetingRoomModel("Conference Room B", "LA Office", office2));

        // Log office and room information for debugging
        logger.info("Saved office 1 with ID: " + office1.getId());
        logger.info("Saved office 2 with ID: " + office2.getId());
    }


    @Test
    void getAllMeetingRooms_returnsRoomsList() throws Exception {

        mockMvc.perform(get("/meetingRooms"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data", hasSize(2)))
                .andExpect(jsonPath("$.data[0].roomName").value("Conference Room A"))
                .andExpect(jsonPath("$.data[1].roomName").value("Conference Room B"));
    }

    @Test
    void getMeetingRoomById_givenRoomId_returnsRoom() throws Exception {
        // Creating a new office and room
        OfficeModel office2 = new OfficeModel("Microsoft", "Bengaluru");
        officeRepo.save(office2);
        MeetingRoomModel room = meetingRoomRepo.save(new MeetingRoomModel("Board Room", "SF Office", office2));

        mockMvc.perform(get("/meetingRooms/" + room.getRoomId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.roomName").value("Board Room"))
                .andExpect(jsonPath("$.roomLocation").value("SF Office"))
                .andExpect(jsonPath("$.officeId").value(office2.getId())); // Use the office ID from the saved office
    }

    @Test
    void addMeetingRoom_givenRoomDetails_createsNewRoom() throws Exception {
        // Persist office to get a valid ID
        OfficeModel office = officeRepo.save(new OfficeModel("Boston Office", "Boston"));

        // Use the generated office ID
        String newRoomJson = "{ \"roomName\": \"Training Room\", \"roomLocation\": \"Boston Office\", \"officeId\": " + office.getId() + " }";

        mockMvc.perform(post("/meetingRooms")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(newRoomJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Meeting Room added successfully"))
                .andExpect(jsonPath("$.code").value(201))
                .andExpect(jsonPath("$.data").value("Success"));
    }


    @Test
    void deleteMeetingRoom_givenRoomId_removesRoom() throws Exception {
        // Step 1: Save Office first
        OfficeModel office = new OfficeModel("Chicago Office", "Chicago");
        office = officeRepo.saveAndFlush(office); // Ensure Office is persisted

        // Step 2: Save Meeting Room with the persisted Office
        MeetingRoomModel room = new MeetingRoomModel("Focus Room", "Chicago Office", office);
        room = meetingRoomRepo.saveAndFlush(room); // Ensure Room is persisted

        // Step 3: Perform DELETE request
        mockMvc.perform(delete("/meetingRooms/" + room.getRoomId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Meeting Room deactivated successfully"))
                .andExpect(jsonPath("$.code").value(200));
    }


    @Test
    void deleteMeetingRoom_givenNonExistRoomId_throwsNotFoundException() throws Exception {
        mockMvc.perform(delete("/meetingRooms/9999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value(404))
                .andExpect(jsonPath("$.message").value("Meeting Room not found"));
    }

    @Test
    void updateMeetingRoomAvailability_givenRoomId_updatesAvailability() throws Exception {
        // Create room with associated office
        OfficeModel office = new OfficeModel("Berlin Office", "Berlin");
        officeRepo.save(office);
        MeetingRoomModel room = meetingRoomRepo.save(new MeetingRoomModel("Quiet Room", "Berlin Office", office));

        String requestBody = "{ \"availability\": true }";

        mockMvc.perform(put("/meetingRooms/" + room.getRoomId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Meeting Room availability updated successfully"))
                .andExpect(jsonPath("$.code").value(200));
    }

    @Test
    void updateMeetingRoomAvailability_givenNonExistingRoomId_throwsNotFoundException() throws Exception {
        int nonExistingRoomId = 23;
        String requestBody = "{ \"availability\": true }";

        mockMvc.perform(put("/meetingRooms/" + nonExistingRoomId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value(404))
                .andExpect(jsonPath("$.message").value("Meeting Room not found"));
    }
}
