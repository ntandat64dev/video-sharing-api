package com.example.videosharingapi.payload.response;

import com.example.videosharingapi.payload.UserDto;

public record AuthResponse(String message, UserDto userInfo) {
}
