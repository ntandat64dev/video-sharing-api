package com.example.videosharingapi.service;

import com.example.videosharingapi.config.security.AuthenticatedUser;
import com.example.videosharingapi.dto.UserDto;

import java.util.List;

public interface UserService {

    UserDto getUserById(String userId);

    List<String> getBrowseKeywords(String userId);

    AuthenticatedUser getAuthenticatedUser();
}