package com.example.videosharingapi.service;

import com.example.videosharingapi.dto.FollowDto;
import com.example.videosharingapi.dto.response.PageResponse;
import org.springframework.data.domain.Pageable;

public interface FollowService {

    PageResponse<FollowDto> getFollowsByFollowerId(String followerId, Pageable pageable);

    FollowDto getFollowsByUserIdAndFollowerId(String userId, String followerId);

    FollowDto follow(FollowDto followDto);

    void unfollow(String followId);
}