package com.example.CalendarManagement.repository;

import com.example.CalendarManagement.model.EmployeeModel;
import com.example.CalendarManagement.model.MeetingRoomModel;
import com.example.CalendarManagement.model.OfficeModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface MeetingRoomRepo extends JpaRepository<MeetingRoomModel, Integer> {
    @Query("SELECT m FROM MeetingRoomModel m WHERE m.available = true")
    List<MeetingRoomModel> findActiveMeetingRooms();

    List<MeetingRoomModel> findByOffice(OfficeModel officeModel);

    void deleteById(int roomId);



}
