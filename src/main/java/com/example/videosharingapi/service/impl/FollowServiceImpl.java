package com.example.videosharingapi.service.impl;

import com.example.videosharingapi.dto.FollowDto;
import com.example.videosharingapi.exception.AppException;
import com.example.videosharingapi.exception.ErrorCode;
import com.example.videosharingapi.mapper.FollowMapper;
import com.example.videosharingapi.repository.FollowRepository;
import com.example.videosharingapi.service.FollowService;
import com.example.videosharingapi.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class FollowServiceImpl implements FollowService {

    private final UserService userService;
    private final FollowRepository followRepository;
    private final FollowMapper followMapper;

    @Override
    public List<FollowDto> getFollowsOfUserId(String userId) {
        return followRepository.findAllByUserId(userId).stream()
                .map(followMapper::toFollowDto)
                .collect(Collectors.toList());
    }

    @Override
    public FollowDto getFollowsByUserIdAndFollowerId(String userId, String followerId) {
        var follow = followRepository.findByUserIdAndFollowerId(userId, followerId);
        return followMapper.toFollowDto(follow);
    }

    @Override
    public FollowDto follow(FollowDto followDto) {
        if (!userService.getAuthenticatedUser().getUserId().equals(followDto.getFollowerSnippet().getUserId())) {
            // If follower is not authenticated user ID.
            throw new AppException(ErrorCode.FORBIDDEN);
        }

        if (followRepository.existsByUserIdAndFollowerId(
                followDto.getSnippet().getUserId(),
                followDto.getFollowerSnippet().getUserId()
        )) throw new AppException(ErrorCode.FOLLOW_EXISTS);

        if (Objects.equals(
                followDto.getSnippet().getUserId(),
                followDto.getFollowerSnippet().getUserId())
        ) throw new AppException(ErrorCode.SELF_FOLLOW);

        var follow = followMapper.toFollow(followDto);
        follow.setPublishedAt(LocalDateTime.now());
        followRepository.save(follow);
        return followMapper.toFollowDto(follow);
    }

    // TODO: TEST
    @Override
    public void unfollow(String followId) {
        var follow = followRepository.findById(followId).orElseThrow();
        if (!userService.getAuthenticatedUser().getUserId().equals(follow.getFollower().getId())) {
            // If current user did not create this follow.
            throw new AppException(ErrorCode.FORBIDDEN);
        }
        followRepository.deleteById(followId);
    }

}
