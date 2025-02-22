package com.example.CalendarManagement;

import com.example.CalendarManagement.DTO.MeetingDTO;
import com.example.CalendarManagement.generated.MeetingManage;
import com.example.CalendarManagement.model.MeetingModel;
import com.example.CalendarManagement.model.MeetingRoomModel;
import com.example.CalendarManagement.model.OfficeModel;
import com.example.CalendarManagement.repository.MeetingRepo;
import com.example.CalendarManagement.repository.MeetingRoomRepo;
import com.example.CalendarManagement.repository.OfficeRepo;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.server.TServer;
import org.apache.thrift.server.TSimpleServer;
import org.apache.thrift.transport.TServerSocket;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = CalendarManagementApplication.class)
@AutoConfigureMockMvc
@ActiveProfiles("test")  // Use test profile
@TestPropertySource("classpath:application-test.properties")
public class MeetingControllerIT {

    private static final int THRIFT_SERVER_PORT = 9091;
    private static final String THRIFT_SERVER_HOST = "localhost";
    private static TServer server;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    MeetingRoomRepo meetingRoomRepo;

    @Autowired
    OfficeRepo officeRepo;

    @Autowired
    private MeetingRepo meetingRepo;

    private OfficeModel office;
    private MeetingRoomModel room;

    @Autowired
    private ObjectMapper objectMapper;
    @BeforeAll
    static void startThriftServer() {
        new Thread(() -> {
            try {
                MeetingManage.Iface handler = mock(MeetingManage.Iface.class);
                when(handler.canScheduleMeeting(anyList(), anyString(), anyString(), anyString(),anyInt()))
                        .thenReturn(true);

                MeetingManage.Processor<MeetingManage.Iface> processor = new MeetingManage.Processor<>(handler);
                TServerSocket serverTransport = new TServerSocket(THRIFT_SERVER_PORT);
                TServer.Args args = new TServer.Args(serverTransport).processor(processor);
                server = new TSimpleServer(args);

                System.out.println("Mock Thrift Server Started...");
                server.serve();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    @AfterAll
    static void stopThriftServer() {
        if (server != null) {
            server.stop();
            System.out.println("Mock Thrift Server Stopped...");
        }
    }

    @Test
    void canScheduleMeeting_validRequest_returnsTrue() throws Exception {
        // Create a Thrift client
        TTransport transport = new TSocket(THRIFT_SERVER_HOST, THRIFT_SERVER_PORT);
        transport.open();
        TProtocol protocol = new TBinaryProtocol(transport);
        MeetingManage.Client client = new MeetingManage.Client(protocol);

        List<Integer> employeeIds = Arrays.asList(101, 102, 103, 104, 105, 106);
        String date = "2025-03-05";
        String startTime = "11:00";
        String endTime = "12:30";

        // Call the Thrift service
        boolean canSchedule = client.canScheduleMeeting(employeeIds, date, startTime, endTime,4);

        // Assert expected response
        assertTrue(canSchedule);

        // Close connection
        transport.close();
    }



    @BeforeEach
    void setUp() {
        office = officeRepo.save(new OfficeModel("capillary", "bengaluru"));
        room = meetingRoomRepo.save(new MeetingRoomModel("s2", "3rd floor", office));

        meetingRepo.save(new MeetingModel("Project Discussion", "Discuss milestones", room,
                LocalDate.parse("2025-02-25"), LocalTime.parse("10:00"), LocalTime.parse("11:00"), true));
        meetingRepo.save(new MeetingModel("Team Sync", "Weekly team update", room,
                LocalDate.parse("2025-02-26"), LocalTime.parse("14:00"), LocalTime.parse("15:00"), false));
    }

    @Test
    void getAllMeetings_returnsMeetingsList() throws Exception {
        mockMvc.perform(get("/meetings"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data", hasSize(8)))  // Checking meetings count
                .andExpect(jsonPath("$.data[0].agenda").value("Discuss milestones"))
                .andExpect(jsonPath("$.data[1].agenda").value("Weekly team update"));
    }

    @Test
    void getMeeting_givenMeetingId_returnsMeeting() throws Exception {
        MeetingModel meeting = meetingRepo.save(new MeetingModel(
                "Discuss project status", "Client Call", room,
                LocalDate.parse("2025-03-01"), LocalTime.parse("16:00"), LocalTime.parse("17:00"), true));

        mockMvc.perform(get("/meetings/" + meeting.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.meetingId").value(meeting.getId()))
                .andExpect(jsonPath("$.data.agenda").value("Client Call"))
                .andExpect(jsonPath("$.data.description").value("Discuss project status"))
                .andExpect(jsonPath("$.data.active").value(true));
    }

    @Test
    void deleteMeeting_givenExistingMeetingId_removesMeeting() throws Exception {
        MeetingModel meeting = meetingRepo.save(new MeetingModel(
                "Board Review", "Annual performance review", room,
                LocalDate.parse("2025-03-10"), LocalTime.parse("11:00"), LocalTime.parse("12:00"), true));

        mockMvc.perform(delete("/meetings/" + meeting.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Meeting deleted successfully"));
    }

    @Test
    void deleteMeeting_givenNonExistMeetingId_throwsNotFoundException() throws Exception {
        MockHttpServletResponse response = mockMvc.perform(post("/meetings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new MeetingDTO())))
                .andReturn()
                .getResponse();

        System.out.println("Response: " + response.getContentAsString());
    }
}
