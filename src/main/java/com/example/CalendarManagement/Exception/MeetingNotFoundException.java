package com.example.CalendarManagement.Exception;

public class MeetingNotFoundException extends RuntimeException{
    public MeetingNotFoundException(String message) {
        super(message);
    }
}
