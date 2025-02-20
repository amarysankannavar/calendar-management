package com.example.CalendarManagement;

import com.example.CalendarManagement.Exception.RoomNotFoundException;
import com.example.CalendarManagement.generated.Meeting;
import com.example.CalendarManagement.generated.MeetingManage;
import com.example.CalendarManagement.model.EmployeeModel;
import com.example.CalendarManagement.model.MeetingModel;
import com.example.CalendarManagement.model.MeetingRoomModel;
import com.example.CalendarManagement.model.MeetingStatusModel;
import com.example.CalendarManagement.repository.EmployeeRepo;
import com.example.CalendarManagement.repository.MeetingRepo;
import com.example.CalendarManagement.repository.MeetingRoomRepo;
import com.example.CalendarManagement.repository.MeetingStatusRepo;
import org.apache.thrift.TException;
import org.apache.thrift.transport.TTransport;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@SpringBootApplication
//public class CalendarManagementApplication implements CommandLineRunner
public class CalendarManagementApplication {
	@Autowired
	private MeetingRepo meetingRepo;

	@Autowired
	private MeetingStatusRepo meetingStatusRepo;

	@Autowired
	private EmployeeRepo employeeRepo;

	@Autowired
	private MeetingRoomRepo meetingRoomRepo;
	private static final Logger logger = LoggerFactory.getLogger(CalendarManagementApplication.class);

	public static void main(String[] args) {
		SpringApplication.run(CalendarManagementApplication.class, args);
	}
	/*

	@Override
	public void run(String... args) throws Exception {
		// Example logic to call MeetingService from CalendarThriftService
		String description = "updates of the work flow3";
		String agenda = "update discussion3";
		List<Integer> employeeIds = Arrays.asList(1, 2, 3);
		String startTime = "11:00:00";
		String endTime = "12:00:00";
		String date = "2025-02-28";

		int roomId = 3;



		// Creating a connection to the Thrift server (CalendarThriftService) running on port 9090
		try (TTransport transport = new TSocket("localhost", 9090)) {
			transport.open();  // Open the transport connection

			TProtocol protocol = new TBinaryProtocol(transport);
			MeetingService.Client client = new MeetingService.Client(protocol);

			try {
				// Scheduling a meeting
				int meetingId = client.scheduleMeeting(description, agenda, employeeIds, date, startTime, endTime, roomId);
				Meeting meeting = client.getMeetingDetails(meetingId);


				//meetingRepo.save(meeting);

				logger.info("Scheduled Meeting ID: " + meeting.description);
				logger.info("Scheduled Meeting ID: " + meeting.employeeIds);
				logger.info("Scheduled Meeting ID: " + meeting.meetingId);

				// Fetching meeting details


				MeetingRoomModel meetingRoom = null;
				try {
					meetingRoom = meetingRoomRepo.findById(roomId)
							.orElseThrow(() -> new RoomNotFoundException("Meeting Room not found for ID: " + roomId));
				} catch (RoomNotFoundException e) {
					logger.error(e.getMessage());
					return;
				}
				MeetingModel meetingModel = new MeetingModel(meeting.meetingId, meeting.description, meeting.agenda,meetingRoom,meeting.date, meeting.startTime, meeting.endTime, true);
				meetingRepo.save(meetingModel);

				for(int i=0;i<meeting.employeeIds.size();i++){
				EmployeeModel employeeModel = employeeRepo.findEmployeeById(meeting.employeeIds.get(i));
					MeetingStatusModel meetingStatusModel = new MeetingStatusModel(employeeModel,meetingModel,"Accepted");
					meetingStatusRepo.save(meetingStatusModel);
				}




				logger.info("Meeting Details: " + meeting.getDescription());

				List<Integer> employeeId = Arrays.asList(1, 2, 3,4,5,6,7,8);
				boolean canSchedule=client.canScheduleMeeting(employeeIds,date,startTime,endTime);
				logger.info("The meeting can be schedule: "+canSchedule);
			} catch (TException e) {
				logger.error("Error occurred while calling Thrift service: " + e.getMessage());
			}
		} catch (TException e) {
			logger.error("Unable to connect to Thrift server: " + e.getMessage());
		}

    }*/
}
