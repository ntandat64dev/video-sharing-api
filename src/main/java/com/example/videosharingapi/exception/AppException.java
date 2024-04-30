package com.example.videosharingapi.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public final class AppException extends RuntimeException {

    private final HttpStatus httpStatus;

    private final String message;

    public AppException(HttpStatus httpStatus, String message) {
        super(message);
        this.httpStatus = httpStatus;
        this.message = message;
    }
}
