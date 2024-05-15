package com.example.videosharingapi.service.impl;

import com.example.videosharingapi.dto.FollowDto;
import com.example.videosharingapi.dto.response.PageResponse;
import com.example.videosharingapi.exception.AppException;
import com.example.videosharingapi.exception.ErrorCode;
import com.example.videosharingapi.mapper.FollowMapper;
import com.example.videosharingapi.repository.FollowRepository;
import com.example.videosharingapi.service.FollowService;
import com.example.videosharingapi.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Objects;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class FollowServiceImpl implements FollowService {

    private final UserService userService;
    private final FollowRepository followRepository;
    private final FollowMapper followMapper;

    @Override
    public PageResponse<FollowDto> getFollowsByFollowerId(String followerId, Pageable pageable) {
        var followDtoPage = followRepository.findAllByFollowerId(followerId, pageable)
                .map(followMapper::toFollowDto);
        return new PageResponse<>(followDtoPage);
    }

    @Override
    public FollowDto getFollowsByUserIdAndFollowerId(String userId, String followerId) {
        var follow = followRepository.findByUserIdAndFollowerId(userId, followerId);
        return followMapper.toFollowDto(follow);
    }

    @Override
    @Transactional
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

    @Override
    @Transactional
    public void unfollow(String followId) {
        var follow = followRepository.findById(followId).orElseThrow();
        if (!userService.getAuthenticatedUser().getUserId().equals(follow.getFollower().getId())) {
            // If current user did not create this follow.
            throw new AppException(ErrorCode.FORBIDDEN);
        }
        followRepository.deleteById(followId);
    }

}
