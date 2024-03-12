package com.example.videosharingapi.service;

import com.example.videosharingapi.payload.dto.AuthDTO;

public interface AuthService {
    String signIn(AuthDTO authDTO);

    String signUp(AuthDTO authDTO);
}
