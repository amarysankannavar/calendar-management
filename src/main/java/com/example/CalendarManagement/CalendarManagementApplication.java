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

}
