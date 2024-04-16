package com.example.videosharingapi.dto.response;

import org.springframework.http.HttpStatus;

import java.sql.Timestamp;

public record ErrorResponse(HttpStatus httpStatus, String errorMessage, Timestamp timestamp) {
}