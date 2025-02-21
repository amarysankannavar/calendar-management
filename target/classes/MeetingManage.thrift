namespace java com.example.CalendarManagement.generated

// Define Meeting Structure
struct Meeting {
    1: i32 meetingId,
    2: string description,
    3: string agenda,
    4: i32 meetingRoomId,
    5: string date,         // Format: "YYYY-MM-DD"
    6: string startTime,    // Format: "HH:MM"
    7: string endTime,      // Format: "HH:MM"
    8: bool isActive,
    9: list<i32> employeeIds  // Store only employee IDs (linked via MeetingStatus)
}


// Enum for Meeting Status
enum MeetingStatus {
    PENDING,
    ACCEPTED,
    DECLINED,
    CANCELLED
}

// Employee-Meeting Relationship (Stored in MeetingStatus Table)
struct EmployeeMeetingStatus {
    1: i32 employeeId,
    2: i32 meetingId,
    3: MeetingStatus status
}

exception MeetingException {
  1: string message,
  2: i32 errorCode
}

// Meeting Service Definition
service MeetingManage {

    // Check if meeting can be scheduled
    bool canScheduleMeeting(1: list<i32> employeeIds, 2: string date, 3: string startTime, 4: string endTime,5: i32 roomId) throws (1: MeetingException ex)

    // Schedule a meeting (returns Meeting ID)
   i32 scheduleMeeting(1: string description, 2: string agenda, 3: list<i32> employeeIds,4: string date, 5: string startTime, 6: string endTime, 7: i32 roomId)  throws (1: MeetingException ex)

    // Cancel an existing meeting
    void cancelMeeting(1: i32 meetingId)

    // Update Employee Meeting Status (Accept/Decline)
    void updateMeetingStatus(1: EmployeeMeetingStatus statusUpdate)

    // Fetch Meeting Details (Includes Employee IDs but not full Employee Data)
    Meeting getMeetingDetails(1: i32 meetingId)

    // Fetch Meetings for an Employee (today, current_week, last_week, custom)
    list<Meeting> getMeetings(1: i32 employeeId, 2: string filterType)

    // Fetch Meeting Status of Employees for a Given Meeting
    list<EmployeeMeetingStatus> getMeetingStatuses(1: i32 meetingId)
}
