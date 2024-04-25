package com.example.videosharingapi.service;

import com.example.videosharingapi.dto.UserDto;

public interface AuthService {
    UserDto login(String email, String password);

    UserDto signup(String email, String password);
}
