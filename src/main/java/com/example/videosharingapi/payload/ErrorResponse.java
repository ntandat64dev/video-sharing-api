package com.example.videosharingapi.payload;

import java.sql.Timestamp;

public record ErrorResponse(int statusCode, String errorMessage, Timestamp timestamp) {
}