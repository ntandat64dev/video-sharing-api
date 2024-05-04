package com.example.videosharingapi.controller;

import com.example.videosharingapi.dto.FollowDto;
import com.example.videosharingapi.dto.UserDto;
import com.example.videosharingapi.entity.Follow;
import com.example.videosharingapi.entity.User;
import com.example.videosharingapi.service.UserService;
import com.example.videosharingapi.validation.IdExists;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/v1/users")
@Validated
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping
    public ResponseEntity<UserDto> getUser(String userId) {
        var response = userService.getUserById(userId);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/follows")
    public ResponseEntity<List<FollowDto>> getFollowsByUserId(
            @IdExists(entity = User.class) String userId,
            @RequestParam(required = false) @IdExists(entity = User.class) String forUserId
    ) {
        List<FollowDto> response = new ArrayList<>();
        if (forUserId != null) {
            var follow = userService.getFollowsByFollowerIdAndUserId(userId, forUserId);
            response.add(follow);
        } else {
            response.addAll(userService.getFollowsByUserId(userId));
        }
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping("/follows")
    public ResponseEntity<FollowDto> followUser(@RequestBody @Validated FollowDto followDto) {
        var response = userService.follow(followDto);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @DeleteMapping("/follows")
    public ResponseEntity<?> unfollowUser(@IdExists(entity = Follow.class) @NotNull String id) {
        userService.unfollow(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping("/video-categories")
    public List<String> getBrowseKeywords(@IdExists(entity = User.class) String userId) {
        return userService.getBrowseKeywords(userId);
    }
}
