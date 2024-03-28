package com.example.videosharingapi.service;

import com.example.videosharingapi.payload.request.AuthRequest;
import com.example.videosharingapi.payload.response.AuthResponse;

public interface AuthService {
    AuthResponse signIn(AuthRequest request);

    AuthResponse signUp(AuthRequest request);
}
