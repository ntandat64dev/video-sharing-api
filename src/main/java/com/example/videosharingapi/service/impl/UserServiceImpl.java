package com.example.videosharingapi.service.impl;

import com.example.videosharingapi.dto.FollowDto;
import com.example.videosharingapi.dto.UserDto;
import com.example.videosharingapi.entity.Hashtag;
import com.example.videosharingapi.exception.AppException;
import com.example.videosharingapi.exception.ErrorCode;
import com.example.videosharingapi.mapper.FollowMapper;
import com.example.videosharingapi.mapper.UserMapper;
import com.example.videosharingapi.repository.FollowRepository;
import com.example.videosharingapi.repository.HashtagRepository;
import com.example.videosharingapi.repository.UserRepository;
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
public class UserServiceImpl implements UserService {
    private final HashtagRepository hashtagRepository;
    private final FollowRepository followRepository;
    private final UserRepository userRepository;

    private final FollowMapper followMapper;
    private final UserMapper userMapper;

    @Override
    public UserDto getUserById(String userId) {
        return userRepository.findById(userId)
                .map(userMapper::toUserDto)
                .orElse(null);
    }

    @Override
    public List<FollowDto> getFollowsByUserId(String userId) {
        return followRepository.findAllByUserId(userId).stream()
                .map(followMapper::toFollowDto)
                .collect(Collectors.toList());
    }

    @Override
    public FollowDto getFollowsByFollowerIdAndUserId(String followerId, String userId) {
        var follow = followRepository.findByFollowerIdAndUserId(followerId, userId);
        return followMapper.toFollowDto(follow);
    }

    @Override
    public FollowDto follow(FollowDto followDto) {
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
    public void unfollow(String followId) {
        followRepository.deleteById(followId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<String> getBrowseKeywords(String userId) {
        // TODO
        return hashtagRepository.findAllByUserId(userId).stream()
                .map(Hashtag::getTag)
                .collect(Collectors.toList());
    }
}