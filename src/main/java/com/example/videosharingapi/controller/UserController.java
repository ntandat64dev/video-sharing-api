package com.example.videosharingapi.controller;

import com.example.videosharingapi.config.security.AuthenticatedUser;
import com.example.videosharingapi.dto.UserDto;
import com.example.videosharingapi.service.UserService;
import com.example.videosharingapi.validation.ValidFile;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/users")
@Validated
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @PostMapping("/profile-image")
    public ResponseEntity<UserDto> changeProfileImage(
            @RequestParam @ValidFile(type = "image") MultipartFile imageFile,
            @AuthenticationPrincipal AuthenticatedUser user
    ) {
        var response = userService.changeProfileImage(imageFile, user.getUserId());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/mine")
    public ResponseEntity<UserDto> getUser(@AuthenticationPrincipal AuthenticatedUser user) {
        var response = userService.getUserById(user.getUserId());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<UserDto> getUserByUserId(@PathVariable String userId) {
        var response = userService.getUserById(userId);
        return ResponseEntity.ok(response);
    }
}
