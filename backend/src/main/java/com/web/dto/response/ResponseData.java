package com.web.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ResponseData<T> {

    private boolean success;
    private int status;
    private String message;
    private T data;
    private LocalDateTime timestamp;

    public ResponseData() {
        this.timestamp = LocalDateTime.now();
    }

    public ResponseData(boolean success, int status, String message) {
        this.success = success;
        this.status = status;
        this.message = message;
        this.timestamp = LocalDateTime.now();
    }

    public ResponseData(boolean success, int status, String message, T data) {
        this.success = success;
        this.status = status;
        this.message = message;
        this.data = data;
        this.timestamp = LocalDateTime.now();
    }

    // Factory methods tiện lợi
    public static <T> ResponseData<T> success(T data, String message) {
        return new ResponseData<>(true, 200, message, data);
    }

    public static <T> ResponseData<T> error(int status, String message) {
        return new ResponseData<>(false, status, message, null);
    }
}
