package com.example.videosharingapi.payload.response;

import org.springframework.http.HttpStatus;

import java.sql.Timestamp;

public record ErrorResponse(HttpStatus httpStatus, String errorMessage, Timestamp timestamp) {
}