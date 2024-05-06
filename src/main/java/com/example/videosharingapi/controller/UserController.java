package com.example.videosharingapi.controller;

import com.example.videosharingapi.config.security.AuthenticatedUser;
import com.example.videosharingapi.dto.UserDto;
import com.example.videosharingapi.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/users")
@Validated
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping("/mine")
    public ResponseEntity<UserDto> getUser(@AuthenticationPrincipal AuthenticatedUser user) {
        var response = userService.getUserById(user.getUserId());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/video-categories/mine")
    public List<String> getBrowseKeywords(@AuthenticationPrincipal AuthenticatedUser user) {
        return userService.getBrowseKeywords(user.getUserId());
    }
}
