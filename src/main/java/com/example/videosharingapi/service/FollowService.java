package com.example.videosharingapi.service;

import com.example.videosharingapi.dto.FollowDto;

import java.util.List;

public interface FollowService {

    List<FollowDto> getFollowsOfUserId(String userId);

    FollowDto getFollowsByUserIdAndFollowerId(String userId, String followerId);

    FollowDto follow(FollowDto followDto);

    void unfollow(String followId);
}