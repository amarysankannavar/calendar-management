package com.example.CalendarManagement;

import com.example.CalendarManagement.generated.MeetingManage;
import com.example.CalendarManagement.generated.MeetingRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import java.util.Arrays;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = CalendarManagementApplication.class)
@AutoConfigureMockMvc
@ActiveProfiles("test")
@TestPropertySource("classpath:application-test.properties")
public class MeetingControllerIT {

    private static final int THRIFT_SERVER_PORT = 9090; // Port where actual Thrift server is running
    private static final String THRIFT_SERVER_HOST = "localhost"; // Update if running on a different host

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private static MeetingManage.Client thriftClient;
    private static TTransport transport;

    @BeforeEach
    void setUp() throws Exception {
        // Ensure the Thrift server is running before executing the test
        transport = new TSocket(THRIFT_SERVER_HOST, THRIFT_SERVER_PORT);
        transport.open();
        TProtocol protocol = new TBinaryProtocol(transport);
        thriftClient = new MeetingManage.Client(protocol);
    }

    @Test
    void canScheduleMeeting_withActualThriftServer_returnsValidRoomId() throws Exception {
        // Prepare MeetingRequest
        MeetingRequest meetingRequest = new MeetingRequest();
        meetingRequest.setEmployeeIds(Arrays.asList(101, 102, 103, 104, 105, 106));
        meetingRequest.setDate("2025-03-05");
        meetingRequest.setStartTime("11:00");
        meetingRequest.setEndTime("12:30");

        int roomId = 1;

        // Call actual Thrift service
        int availableRoomId = thriftClient.canScheduleMeeting(meetingRequest, roomId);

        // Assert expected response (Ensure the actual Thrift server is returning correct values)
        assertEquals(5, availableRoomId); // Adjust expected value based on actual implementation
    }


}
