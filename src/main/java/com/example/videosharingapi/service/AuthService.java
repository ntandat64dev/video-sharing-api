package com.example.videosharingapi.service;

import com.example.videosharingapi.dto.response.AuthResponse;

public interface AuthService {

    AuthResponse login(String username, String password);

    void signup(String username, String password);
}
