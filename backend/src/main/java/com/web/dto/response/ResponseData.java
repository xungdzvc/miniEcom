package com.web.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ResponseData<T> {

    private boolean success;
    private int status;
    private String message;
    private T data;
    private Instant timestamp;

    public ResponseData() {
        this.timestamp = Instant.now();
    }

    public ResponseData(boolean success, int status, String message) {
        this.success = success;
        this.status = status;
        this.message = message;
        this.timestamp = Instant.now();
    }

    public ResponseData(boolean success, int status, String message, T data) {
        this.success = success;
        this.status = status;
        this.message = message;
        this.data = data;
        this.timestamp = Instant.now();
    }

    // Factory methods tiện lợi
    public static <T> ResponseData<T> success(T data, String message) {
        return new ResponseData<>(true, 200, message, data);
    }

    public static <T> ResponseData<T> error(int status, String message) {
        return new ResponseData<>(false, status, message, null);
    }
}
