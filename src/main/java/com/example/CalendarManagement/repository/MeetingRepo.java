package com.example.CalendarManagement.repository;

import com.example.CalendarManagement.model.MeetingModel;
import com.example.CalendarManagement.model.MeetingRoomModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface MeetingRepo extends JpaRepository<MeetingModel, Integer> {

    // Custom query methods can be added as needed
    List<MeetingModel> findByMeetingRoom(MeetingRoomModel meetingRoomModel);

    List<MeetingModel> findByDate(LocalDate date);

    List<MeetingModel> findByIsActive(boolean isActive);





}
