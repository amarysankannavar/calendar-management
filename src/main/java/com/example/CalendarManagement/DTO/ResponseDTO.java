package com.example.CalendarManagement.DTO;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResponseDTO<T> {
    private String message;
    private int code;
    private DataContainer<T> data;
    private Object error;

    public ResponseDTO(String message, int code, T body, Object error) {
        this.message = message;
        this.code = code;
        this.data = new DataContainer<>(body);
        this.error = error;
    }


    private static class DataContainer<T> {
        private T body;

        public DataContainer(T body) {
            this.body = body;
        }

        public T getBody() {
            return body;
        }
    }
}
