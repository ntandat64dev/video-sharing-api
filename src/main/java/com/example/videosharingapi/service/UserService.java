package com.example.videosharingapi.service;

import com.example.videosharingapi.config.security.AuthenticatedUser;
import com.example.videosharingapi.dto.UserDto;

public interface UserService {

    UserDto getUserById(String userId);

    AuthenticatedUser getAuthenticatedUser();
}