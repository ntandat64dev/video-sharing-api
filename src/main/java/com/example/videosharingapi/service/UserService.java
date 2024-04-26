package com.example.videosharingapi.service;

import com.example.videosharingapi.dto.FollowDto;
import com.example.videosharingapi.dto.UserDto;

import java.util.List;
import java.util.UUID;

public interface UserService {
    UserDto getUserById(UUID userId);

    List<FollowDto> getFollowsByUserId(UUID userId);

    FollowDto getFollowsByFollowerIdAndUserId(UUID followerId, UUID userId);

    FollowDto follow(FollowDto followDto);

    void unfollow(UUID followId);

    List<String> getBrowseKeywords(UUID userId);
}
