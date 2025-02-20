package com.example.CalendarManagement.DTO;


import java.util.Map;

public class ApiResponse<T> {
    private String message;
    private int code;
    private T data;
    //private String error;
    private Map<String, String> error;

    public ApiResponse(String message, int code, T data, Map<String, String> error) {
        this.message = message;
        this.code = code;
        this.data = data;
        this.error = error;
    }

    public String getMessage() { return message; }
    public int getCode() { return code; }
    public T getData() { return data; }
    public Map<String, String> getError() { return error; }

    public void setMessage(String message) { this.message = message; }
    public void setCode(int code) { this.code = code; }
    public void setData(T data) { this.data = data; }
    public void setError(Map<String, String> error) { this.error = error; }
}
