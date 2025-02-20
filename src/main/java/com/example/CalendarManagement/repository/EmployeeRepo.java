package com.example.CalendarManagement.repository;

import com.example.CalendarManagement.model.EmployeeModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

public interface EmployeeRepo extends JpaRepository<EmployeeModel,Integer> {

    @Query("SELECT e FROM EmployeeModel e WHERE e.isActive = true")
    List<EmployeeModel> findActiveEmployees();

    @Query("SELECT e FROM EmployeeModel e WHERE e.id = :id")
    EmployeeModel findEmployeeById(Integer id);

    Optional<EmployeeModel> findByWorkEmail(String workEmail);

  /*  @Query("SELECT e.id " +
            "FROM EmployeeModel e " +
            "WHERE e.id IN :employeeIds " +
            "AND e.id NOT IN (" +
            "    SELECT ms.employee.id " +
            "    FROM MeetingStatusModel ms " +
            "    WHERE ms.meeting.id IN (" +
            "        SELECT m.id " +
            "        FROM MeetingModel m " +
            "        WHERE m.date = :date " +
            "        AND m.startTime < :end " +
            "        AND m.endTime > :start" +
            "    )" +
            ")")
    List<Integer> findAvailableEmpoloyees(List<Integer> employeeIds, LocalDate date, LocalTime start, LocalTime end);*/
}
