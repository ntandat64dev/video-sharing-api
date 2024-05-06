package com.example.videosharingapi.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class AuthResponse {
    private String token;
    private LocalDateTime expiration;
}
