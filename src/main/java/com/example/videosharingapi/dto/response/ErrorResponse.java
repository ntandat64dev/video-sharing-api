package com.example.videosharingapi.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public final class ErrorResponse {

    private HttpStatus httpStatus;

    private String message;

    private List<String> errors;

    public ErrorResponse(HttpStatus httpStatus, String message, String error) {
        this(httpStatus, message, List.of(error));
    }

    public ErrorResponse(HttpStatus httpStatus, String message) {
        this(httpStatus, message, List.of());
    }
}