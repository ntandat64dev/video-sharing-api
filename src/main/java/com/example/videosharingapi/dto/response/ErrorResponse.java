package com.example.videosharingapi.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

import java.sql.Timestamp;

@Getter
@Setter
@AllArgsConstructor
public final class ErrorResponse {
    private HttpStatus httpStatus;
    private String errorMessage;
    private Timestamp timestamp;
}