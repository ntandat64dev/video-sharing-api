package com.example.videosharingapi.payload.response;

import com.example.videosharingapi.payload.UserDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public final class AuthResponse {
    private String message;
    private UserDto userInfo;
}
