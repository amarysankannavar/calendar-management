package com.example.CalendarManagement.service;

import com.example.CalendarManagement.Exception.DataStorageException;
import com.example.CalendarManagement.Exception.OfficeNotFoundException;
import com.example.CalendarManagement.model.MeetingRoomModel;
import com.example.CalendarManagement.model.OfficeModel;
import com.example.CalendarManagement.repository.MeetingRoomRepo;
import com.example.CalendarManagement.repository.OfficeRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@Service
public class OfficeService {

    @Autowired
    private OfficeRepo officeRepo;

    @Autowired
    private MeetingRoomRepo meetingRoomRepo;

    @Autowired
    private MeetingRoomService meetingRoomService;

    // Method to find an office by name
    public OfficeModel findOfficeByName(String name) {
        return officeRepo.findByName(name)
                .orElseThrow(() -> new RuntimeException("Office not found with name: " + name));
    }


    // Method to create a new office
    public OfficeModel createOffice(String name, String location) {
        try{
            OfficeModel newOffice = new OfficeModel(name, location);
            return officeRepo.save(newOffice);
        } catch (Exception e) {
            throw new DataStorageException("Failed to add the Office Room.");
        }
    }

    public List<OfficeModel> getAllOffices() {
        return officeRepo.findAll();
    }

    @Transactional
    public boolean deleteOffice(int id) {
        OfficeModel office = officeRepo.findById(id)
                .orElseThrow(() -> new OfficeNotFoundException("Office not found"));

        List<MeetingRoomModel> meetingRooms = meetingRoomRepo.findByOffice(office);
        for (MeetingRoomModel room : meetingRooms){
            meetingRoomService.deleteMeetingRoom(room.getRoomId());
        }

        officeRepo.delete(office);

        return true;
    }

} 