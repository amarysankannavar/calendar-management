package com.example.CalendarManagement.mapper;

import com.example.CalendarManagement.DTO.MeetingDTO;
import com.example.CalendarManagement.model.MeetingModel;
import com.example.CalendarManagement.model.MeetingRoomModel;
//import com.example.CalendarManagement.generated.Meeting;

import java.util.List;
import java.util.stream.Collectors;

public class MeetingMapper {



    // Convert Meeting entity to MeetingDTO
    public static MeetingDTO convertEntityToDto(MeetingModel meeting) {
        if (meeting == null) {
            return null;
        }
        return new MeetingDTO(
                meeting.getId(),
                meeting.getDescription(),
                meeting.getAgenda(),
                meeting.getMeetingRoom().getRoomId(),
                meeting.getDate(),
                meeting.getStartTime(),
                meeting.getEndTime(),
                meeting.isActive()
        );
    }

    // Convert MeetingDTO to Meeting entity
    public static MeetingModel convertDtoToEntity(MeetingDTO meetingDTO, MeetingRoomModel meetingRoom) {
        if (meetingDTO == null) {
            return null;
        }
        MeetingModel meeting = new MeetingModel();
        meeting.setId(meetingDTO.getMeetingId());
        meeting.setDescription(meetingDTO.getDescription());
        meeting.setAgenda(meetingDTO.getAgenda());
        meeting.setMeetingRoom(meetingRoom);
        meeting.setDate(meetingDTO.getDate());
        meeting.setStartTime(meetingDTO.getStartTime());
        meeting.setEndTime(meetingDTO.getEndTime());
        meeting.setActive(meetingDTO.isActive());
        return meeting;
    }

    // Convert a list of Meeting entities to MeetingDTOs
    public static List<MeetingDTO> convertEntityListToDtoList(List<MeetingModel> meetings) {
        return meetings.stream().map(MeetingMapper::convertEntityToDto).collect(Collectors.toList());
    }
}
