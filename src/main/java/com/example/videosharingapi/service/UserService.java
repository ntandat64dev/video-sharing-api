package com.example.videosharingapi.service;

import com.example.videosharingapi.config.security.AuthenticatedUser;
import com.example.videosharingapi.dto.UserDto;

public interface UserService {

    void createUser(String username, String password);

    UserDto getUserById(String userId);

    AuthenticatedUser getAuthenticatedUser();
}