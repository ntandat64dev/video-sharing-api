package com.example.videosharingapi.service;

import com.example.videosharingapi.dto.FollowDto;
import com.example.videosharingapi.dto.UserDto;

import java.util.List;

public interface UserService {
    UserDto getUserById(String userId);

    List<FollowDto> getFollowsByUserId(String userId);

    FollowDto getFollowsByFollowerIdAndUserId(String followerId, String userId);

    FollowDto follow(FollowDto followDto);

    void unfollow(String followId);

    List<String> getBrowseKeywords(String userId);
}
