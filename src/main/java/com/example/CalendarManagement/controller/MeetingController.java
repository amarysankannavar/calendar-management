package com.example.CalendarManagement.controller;

import com.example.CalendarManagement.DTO.ApiResponse;
import com.example.CalendarManagement.DTO.MeetingDTO;
import com.example.CalendarManagement.DTO.MeetingRequestDTO;
import com.example.CalendarManagement.DTO.ScheduleMeetingDTO;
import com.example.CalendarManagement.generated.MeetingResponse;
import com.example.CalendarManagement.service.MeetingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.persistence.criteria.CriteriaBuilder;
import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@Validated
@RequestMapping("/meetings")
public class MeetingController {

    @Autowired
    private MeetingService meetingService;

    // Get all meetings
    @GetMapping
    public ApiResponse<List<MeetingDTO>> getAllMeetings() {
        List<MeetingDTO> meetings = meetingService.getMeetings();
        return new ApiResponse<>("Meetings fetched successfully", 200, meetings, null);
    }

    // Get meeting by ID
    @GetMapping("/{meetingId}")
    public ResponseEntity<ApiResponse<MeetingDTO>> getMeetingById(@PathVariable int meetingId) {
        MeetingDTO meeting = meetingService.getMeetingById(meetingId);
        return ResponseEntity.ok(new ApiResponse<>("Meeting fetched successfully", 200, meeting, null));
    }



    // Deactivate (delete) meeting
    @DeleteMapping("/{meetingId}")
    public ResponseEntity<ApiResponse<String>> deleteMeeting(@PathVariable int meetingId) {
        boolean isDeleted = meetingService.cancelMeeting(meetingId);
        if (isDeleted) {
            return ResponseEntity.ok(new ApiResponse<>("Meeting deleted successfully", 200, "Success", null));
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse<>("Meeting not found", 404, null, null));
    }

    @PostMapping("/canSchedule")
    public ResponseEntity<ApiResponse<String>> canScheduleMeeting(@Valid @RequestBody MeetingRequestDTO meetingRequestDTO){
        int schedule = meetingService.canSchedule(meetingRequestDTO);
        String canScheduleOrNot = schedule!=-1 ? "Can be scheduled. and the available Room id is:"+schedule : "can not schedule.";
        return ResponseEntity.ok(new ApiResponse<>("The meeting availability fetched",200,canScheduleOrNot,null));
    }

    @PostMapping("/scheduleMeeting")
    public ResponseEntity<ApiResponse<String>> scheduleMeeting(@Valid @RequestBody ScheduleMeetingDTO scheduleMeetingDTO){

            MeetingResponse meetAndRoomId = meetingService.scheduleMeeting(scheduleMeetingDTO);
            if(meetAndRoomId.getScheduledMeetingId()==-1){
                Map<String, String> errors = new HashMap();
                errors.put("details:","conflicts error");
                return ResponseEntity.status(HttpStatus.CONFLICT).body(new ApiResponse<>("meeting schedule details",409,"meet is not scheduled",errors));
            }
            return ResponseEntity.ok(new ApiResponse<>("meeting schedule details",200,"meet is scheduled. the meet id is :"+meetAndRoomId.getScheduledMeetingId()+
                    "and the scheduled room id is: "+meetAndRoomId.getAvailableRoomId()+" and the scheduled room name is: "+meetAndRoomId.getRoomName(),null));

    }

    @PostMapping("/cancel/{meetingId}")
    public ResponseEntity<ApiResponse<String>> cancelMeeting(@PathVariable int meetingId) {
        boolean isCancelled = meetingService.cancelMeeting(meetingId);

        if (isCancelled) {
            return ResponseEntity.ok(new ApiResponse<>("Meeting canceled successfully", 200, "Success", null));
        } else {
            return ResponseEntity.badRequest().body(new ApiResponse<>("Failed to cancel meeting", 400, "Error", null));
        }
    }


}
