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



    Optional<EmployeeModel> findByWorkEmail(String workEmail);

}
