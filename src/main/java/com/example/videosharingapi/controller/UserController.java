package com.example.videosharingapi.controller;

import com.example.videosharingapi.service.UserService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/user")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/video-categories")
    public List<String> getBrowseKeywords(UUID userId) {
        return userService.getBrowseKeywords(userId);
    }
}
