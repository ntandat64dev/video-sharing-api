package com.example.videosharingapi.service;

import com.example.videosharingapi.dto.UserDto;

public interface AuthService {
    UserDto login(String username, String password);

    UserDto signup(String username, String password);
}
