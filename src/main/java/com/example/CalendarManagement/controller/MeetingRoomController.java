package com.example.CalendarManagement.controller;

import com.example.CalendarManagement.DTO.ApiResponse;
import com.example.CalendarManagement.DTO.MeetingRoomDTO;
import com.example.CalendarManagement.service.MeetingRoomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;

@RestController
public class MeetingRoomController {

    @Autowired
    MeetingRoomService meetingRoomService;

    @GetMapping("/meetingRooms")
    public ApiResponse<List<MeetingRoomDTO>> getAllMeetingRooms() {
        List<MeetingRoomDTO> rooms = meetingRoomService.getMeetingRooms();
        return new ApiResponse<>("Meeting rooms fetched successfully", 200, rooms, null);
    }

    @PostMapping("/meetingRooms")
    public ApiResponse<String> addMeetingRoom(@Valid @RequestBody MeetingRoomDTO room) {
        meetingRoomService.addMeetingRoom(room);
        return new ApiResponse<>("Meeting Room added successfully", 201, "Success", null);
    }

    @GetMapping("/meetingRooms/{roomId}")
    public MeetingRoomDTO getMeetingRoomById(@PathVariable int roomId) {
        return meetingRoomService.getMeetingRoomById(roomId);
    }

    @DeleteMapping("/meetingRooms/{roomId}")
    public ResponseEntity<ApiResponse<String>> deleteMeetingRoom(@PathVariable int roomId) {

        meetingRoomService.deleteMeetingRoom(roomId);
        return ResponseEntity.ok(new ApiResponse<>("Meeting Room deactivated successfully", 200, "Success", null));

    }

    @PutMapping("/meetingRooms/{roomId}")  // Removed "availability" from the path
    public ResponseEntity<ApiResponse<String>> updateMeetingRoomAvailability(
            @PathVariable int roomId,
            @RequestBody Map<String, Boolean> requestBody) {
       try{
           boolean availability = requestBody.get("availability");
           meetingRoomService.updateMeetingRoomAvailability(roomId, availability);
       } catch (Exception e) {
           throw new RuntimeException(e);
       }
        return ResponseEntity.ok(new ApiResponse<>("Meeting Room availability updated successfully", 200, "Success", null));
    }
}
