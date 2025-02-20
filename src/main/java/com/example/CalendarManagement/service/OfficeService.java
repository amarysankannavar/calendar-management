package com.example.CalendarManagement.service;

import com.example.CalendarManagement.model.OfficeModel;
import com.example.CalendarManagement.repository.OfficeRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OfficeService {

    @Autowired
    private OfficeRepo officeRepo;

    // Method to find an office by name
    public OfficeModel findOfficeByName(String name) {
        return officeRepo.findByName(name)
                .orElseThrow(() -> new RuntimeException("Office not found with name: " + name));
    }


    // Method to create a new office
    public OfficeModel createOffice(String name, String location) {
        OfficeModel newOffice = new OfficeModel(name, location);
        return officeRepo.save(newOffice);
    }

    public List<OfficeModel> getAllOffices() {
        return officeRepo.findAll();
    }

}
