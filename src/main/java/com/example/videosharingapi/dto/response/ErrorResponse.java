package com.example.videosharingapi.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;
import org.springframework.http.HttpStatusCode;

import java.util.List;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public final class ErrorResponse {

    private HttpStatusCode httpStatus;
    private String message;
    private List<String> errors;
}