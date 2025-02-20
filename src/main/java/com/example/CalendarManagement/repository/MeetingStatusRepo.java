package com.example.CalendarManagement.repository;

import com.example.CalendarManagement.model.EmployeeModel;
import com.example.CalendarManagement.model.MeetingModel;
import com.example.CalendarManagement.model.MeetingStatusModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface MeetingStatusRepo extends JpaRepository<MeetingStatusModel, Integer> {

    // You can add custom query methods if needed
    // Example: Find by employee and meeting
    List<MeetingStatusModel> findByEmployee(EmployeeModel employee);

    List<MeetingStatusModel> findByMeeting(MeetingModel meeting);

    // Example: Find by meeting and status
    List<MeetingStatusModel> findByMeetingAndStatus(MeetingModel meeting, String status);

    @Query("SELECT ms.meeting.id, ms.meeting.description, ms.meeting.agenda " +
            "FROM MeetingStatusModel ms " +
            "WHERE ms.employee.id = :employeeId AND ms.meeting.date BETWEEN :fromDate AND :toDate")
    List<Object[]> findMeetingDetailsByEmployeeIdAndDateRange(Integer employeeId,
                                                              @Param("fromDate") LocalDate fromDate,
                                                              @Param("toDate") LocalDate toDate);
}
