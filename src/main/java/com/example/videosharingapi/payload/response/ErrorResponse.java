package com.example.videosharingapi.payload.response;

import java.sql.Timestamp;

public record ErrorResponse(int statusCode, String errorMessage, Timestamp timestamp) {
}