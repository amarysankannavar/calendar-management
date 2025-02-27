package com.example.CalendarManagement.DTO;


/*import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
*/
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
public class ApiResponse<T> {
    private String message;
    private int code;
    private T data;
    private Map<String, String> error;

    public ApiResponse(String message, int code, T data, Map<String, String> error) {
        this.message = message;
        this.code = code;
        this.data = data;
        this.error = error;
    }

}
