package com.example.videosharingapi.controller;

import com.example.videosharingapi.config.validation.IdExistsConstraint;
import com.example.videosharingapi.config.validation.group.OnCreate;
import com.example.videosharingapi.dto.FollowDto;
import com.example.videosharingapi.dto.UserDto;
import com.example.videosharingapi.model.entity.User;
import com.example.videosharingapi.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/users")
@Validated
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public ResponseEntity<UserDto> getUser(UUID userId) {
        var response = userService.getUserById(userId);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/follows")
    public ResponseEntity<List<FollowDto>> getFollowsByUserId(
            @IdExistsConstraint(entity = User.class)
            UUID userId,
            @RequestParam(required = false)
            @IdExistsConstraint(entity = User.class)
            UUID forUserId
    ) {
        List<FollowDto> response = new ArrayList<>();
        if (forUserId != null) {
            var follow = userService.getFollowsByFollowerIdAndUserId(userId, forUserId);
            if (follow != null) {
                response.add(follow);
            }
        } else {
            response.addAll(userService.getFollowsByUserId(userId));
        }
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping("/follows")
    public ResponseEntity<FollowDto> followUser(
            @RequestBody
            @Validated(OnCreate.class)
            FollowDto followDto
    ) {
        var response = userService.follow(followDto);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/video-categories")
    public List<String> getBrowseKeywords(@IdExistsConstraint(entity = User.class) UUID userId) {
        return userService.getBrowseKeywords(userId);
    }
}
