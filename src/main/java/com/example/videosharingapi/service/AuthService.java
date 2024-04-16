package com.example.videosharingapi.service;

import com.example.videosharingapi.dto.UserDto;

public interface AuthService {
    UserDto signIn(String email, String password);

    UserDto signUp(String email, String password);
}
