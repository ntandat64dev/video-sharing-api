package com.example.videosharingapi.controller;

import com.example.videosharingapi.config.security.AuthenticatedUser;
import com.example.videosharingapi.dto.FollowDto;
import com.example.videosharingapi.dto.response.PageResponse;
import com.example.videosharingapi.entity.Follow;
import com.example.videosharingapi.entity.User;
import com.example.videosharingapi.service.FollowService;
import com.example.videosharingapi.validation.IdExists;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/follows")
@Validated
@RequiredArgsConstructor
public class FollowController {
    private final FollowService followService;

    @GetMapping("/mine")
    public ResponseEntity<PageResponse<FollowDto>> getFollows(
            @AuthenticationPrincipal AuthenticatedUser user,
            Pageable pageable
    ) {
        var response = followService.getFollowsByFollowerId(user.getUserId(), pageable);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/for-user")
    public ResponseEntity<FollowDto> getFollowOfUserIdThatFollowedByMe(
            @IdExists(entity = User.class) String userId,
            @AuthenticationPrincipal AuthenticatedUser user
    ) {
        var response = followService.getFollowsByUserIdAndFollowerId(userId, user.getUserId());
        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<FollowDto> followUser(@RequestBody @Valid FollowDto followDto) {
        var response = followService.follow(followDto);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @DeleteMapping
    public ResponseEntity<?> unfollowUser(@IdExists(entity = Follow.class) @NotNull String id) {
        followService.unfollow(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
