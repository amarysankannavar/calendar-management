package com.example.CalendarManagement.controller;

import com.example.CalendarManagement.model.OfficeModel;
import com.example.CalendarManagement.service.OfficeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/offices")  // Base URL for office-related APIs
public class OfficeController {

    @Autowired
    private OfficeService officeService;

    // Get all offices
   @GetMapping
    public ResponseEntity<List<OfficeModel>> getAllOffices() {
        return ResponseEntity.ok(officeService.getAllOffices());
    }

    // Get office by name
    @GetMapping("{name}")
    public ResponseEntity<OfficeModel> getOfficeByName(@PathVariable String name) {
        return ResponseEntity.ok(officeService.findOfficeByName(name));
    }

    // Create a new office
    @PostMapping
    public ResponseEntity<OfficeModel> createOffice(@RequestBody OfficeModel officeModel) {
        OfficeModel createdOffice = officeService.createOffice(officeModel.getName(), officeModel.getOfficeLoc());
        return ResponseEntity.ok(createdOffice);
    }
}
