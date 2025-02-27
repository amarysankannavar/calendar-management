package com.example.CalendarManagement.service;

import com.example.CalendarManagement.DTO.MeetingDTO;
import com.example.CalendarManagement.DTO.MeetingRequestDTO;
import com.example.CalendarManagement.DTO.ScheduleMeetingDTO;
import com.example.CalendarManagement.Exception.EmployeeNotFoundException;
import com.example.CalendarManagement.Exception.MeetingNotFoundException;
import com.example.CalendarManagement.Exception.RoomNotFoundException;
import com.example.CalendarManagement.generated.*;
import com.example.CalendarManagement.mapper.MeetingMapper;
import com.example.CalendarManagement.model.EmployeeModel;
import com.example.CalendarManagement.model.MeetingModel;
import com.example.CalendarManagement.model.MeetingRoomModel;
import com.example.CalendarManagement.repository.EmployeeRepo;
import com.example.CalendarManagement.repository.MeetingRepo;
import com.example.CalendarManagement.repository.MeetingRoomRepo;
import com.example.CalendarManagement.repository.MeetingStatusRepo;
import org.apache.thrift.TException;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;
import org.apache.thrift.transport.TTransportException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class MeetingService {

    @Autowired
    private MeetingRepo meetingRepo;

    @Autowired
    private MeetingRoomRepo meetingRoomRepo;

    @Autowired
    private EmployeeService employeeService;

    @Autowired
    private MeetingStatusRepo meetingStatusRepo;

    @Autowired
    private EmployeeRepo employeeRepo;


    MeetingRequest meetingRequest = new MeetingRequest();


    Logger logger =LoggerFactory.getLogger(MeetingService.class);






    // Fetch all meetings and return as MeetingDTO list
    public List<MeetingDTO> getMeetings() {
        return meetingRepo.findAll().stream()
                .map(MeetingMapper::convertEntityToDto)
                .collect(Collectors.toList());
    }

    // Fetch meeting by ID
    public MeetingDTO getMeetingById(int meetingId) {
        return meetingRepo.findById(meetingId).map(MeetingMapper::convertEntityToDto).orElseThrow(()
                -> new MeetingNotFoundException("Meeting not found"));
    }



    public int canSchedule(MeetingRequestDTO meetingRequestDTO) {

        MDC.put("requestId", UUID.randomUUID().toString());
        logger.info("Checking if meeting can be scheduled, requestId: {}", MDC.get("requestId"));


        List<Integer> empIds = meetingRequestDTO.getEmployeeIds();
        for (int empId : empIds) {
            Optional<EmployeeModel> employee = employeeRepo.findById(empId);
            if(!employee.isPresent() || !employee.get().isActive()){
                throw new EmployeeNotFoundException("Invalid employee ids");
            }

        }



        TTransport transport = null;
        try {
            transport = new TSocket("localhost", 9090);
            transport.open();
            TProtocol protocol = new TBinaryProtocol(transport);
            MeetingManage.Client client = new MeetingManage.Client(protocol);

            meetingRequest.setEmployeeIds(meetingRequestDTO.getEmployeeIds());
            meetingRequest.setDate(String.valueOf(meetingRequestDTO.getDate()));
            meetingRequest.setStartTime(String.valueOf(meetingRequestDTO.getStartTime()));
            meetingRequest.setEndTime(String.valueOf(meetingRequestDTO.getEndTime()));

            int roomId = (meetingRequestDTO.getRoomId() == null) ? -1 : meetingRequestDTO.getRoomId();

            logger.error("the roomId is:"+roomId);



            logger.info("Calling Thrift service to check schedule, requestId: {}", MDC.get("requestId"));
            try {
                int availableRoomId = client.canScheduleMeeting(meetingRequest,roomId);
                logger.info("Thrift service responded with available room ID: {}, requestId: {}", availableRoomId, MDC.get("requestId"));
                return availableRoomId;
            } catch (MeetingException e) {
                throw e;
            } catch (TException e) {
                throw new RuntimeException(e);
            }


        } catch (TTransportException | MeetingException e) {
            throw new RuntimeException(e);
        } finally {
            if (transport != null && transport.isOpen()) {
                transport.close();  // CLOSE TRANSPORT HERE
            }
        }

    }

    public MeetingModel saveMeeting(MeetingModel meeting) {
        return meetingRepo.save(meeting);
    }

    public MeetingResponse scheduleMeeting(ScheduleMeetingDTO scheduleMeetingDTO){
        MeetingInformation meetingInformation = new MeetingInformation();
        TTransport transport = null;
        MeetingResponse meetingAndRoomIds = new MeetingResponse();
        try{
            transport = new TSocket("localhost",9090);
            transport.open();
            TProtocol protocol = new TBinaryProtocol(transport);
            MeetingManage.Client client = new MeetingManage.Client(protocol);


            meetingInformation.setDescription(scheduleMeetingDTO.getDescription());
            meetingInformation.setAgenda(scheduleMeetingDTO.getAgenda());
            meetingRequest.setEmployeeIds(scheduleMeetingDTO.getEmployeeIds());
            meetingRequest.setDate(String.valueOf(scheduleMeetingDTO.getDate()));
            meetingRequest.setStartTime(String.valueOf(scheduleMeetingDTO.getStartTime()));
            meetingRequest.setEndTime(String.valueOf(scheduleMeetingDTO.getEndTime()));

            int roomId = (scheduleMeetingDTO.getRoomId() == null) ? -1 : scheduleMeetingDTO.getRoomId();
            logger.info("room id in schedule meeting:"+roomId);

            try{
                meetingAndRoomIds = client.scheduleMeeting(meetingInformation,meetingRequest,roomId);
                logger.info("after calling the schedule meeting");
            } catch (TException e) {
                throw new RuntimeException(e);
            }
        } catch (TTransportException e) {
            throw new RuntimeException(e);
        } finally {
            if (transport != null && transport.isOpen()) {
                transport.close();  // CLOSE TRANSPORT HERE
            }
        }



        return meetingAndRoomIds;
    }

    @Transactional
    public boolean cancelMeeting(int meetingId) {
        Optional<MeetingModel> meetingOptional = meetingRepo.findById(meetingId);

        if (!meetingOptional.isPresent()) {
            throw new MeetingNotFoundException("Meeting not found with ID: " + meetingId);
        }

        MeetingModel meeting = meetingOptional.get();
        meetingStatusRepo.deleteByMeeting(meeting);
        meeting.setActive(false); // Soft delete
        meetingRepo.save(meeting);

        return true; // Successfully canceled
    }



}
