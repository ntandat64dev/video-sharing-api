package com.example.videosharingapi.service;

import com.example.videosharingapi.config.security.AuthenticatedUser;
import com.example.videosharingapi.dto.UserDto;
import com.example.videosharingapi.entity.User;
import org.springframework.web.multipart.MultipartFile;

public interface UserService {

    User createUser(String username, String password);

    UserDto changeProfileImage(MultipartFile imageFile, String userId);

    UserDto getUserById(String userId);

    AuthenticatedUser getAuthenticatedUser();
}