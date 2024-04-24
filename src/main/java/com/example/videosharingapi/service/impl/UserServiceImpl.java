package com.example.videosharingapi.service.impl;

import com.example.videosharingapi.dto.FollowDto;
import com.example.videosharingapi.dto.UserDto;
import com.example.videosharingapi.exception.ApplicationException;
import com.example.videosharingapi.mapper.FollowMapper;
import com.example.videosharingapi.mapper.UserMapper;
import com.example.videosharingapi.model.entity.Hashtag;
import com.example.videosharingapi.repository.FollowRepository;
import com.example.videosharingapi.repository.HashtagRepository;
import com.example.videosharingapi.repository.UserRepository;
import com.example.videosharingapi.service.UserService;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
public class UserServiceImpl implements UserService {
    private final HashtagRepository hashtagRepository;
    private final FollowRepository followRepository;
    private final UserRepository userRepository;

    private final MessageSource messageSource;
    private final FollowMapper followMapper;
    private final UserMapper userMapper;

    public UserServiceImpl(
            HashtagRepository hashtagRepository, FollowRepository followRepository,
            UserRepository userRepository, MessageSource messageSource, FollowMapper followMapper,
            UserMapper userMapper
    ) {
        this.hashtagRepository = hashtagRepository;
        this.followRepository = followRepository;
        this.userRepository = userRepository;
        this.messageSource = messageSource;
        this.followMapper = followMapper;
        this.userMapper = userMapper;
    }

    @Override
    public UserDto getUserById(UUID userId) {
        if (!userRepository.existsById(userId))
            throw new ApplicationException(HttpStatus.BAD_REQUEST,
                    messageSource.getMessage("exception.user.id.not-exist",
                            new Object[] { userId }, LocaleContextHolder.getLocale()));

        return userRepository.findById(userId)
                .map(userMapper::toUserDto)
                .orElse(null);
    }

    @Override
    public List<FollowDto> getFollowsByUserId(UUID userId) {
        if (!userRepository.existsById(userId))
            throw new ApplicationException(HttpStatus.BAD_REQUEST,
                    messageSource.getMessage("exception.user.id.not-exist",
                            new Object[] { userId }, LocaleContextHolder.getLocale()));

        return followRepository.findAllByUserId(userId).stream()
                .map(followMapper::toFollowDto)
                .collect(Collectors.toList());
    }

    @Override
    public FollowDto getFollowsByFollowerIdAndUserId(UUID followerId, UUID userId) {
        if (!userRepository.existsById(followerId))
            throw new ApplicationException(HttpStatus.BAD_REQUEST,
                    messageSource.getMessage("exception.user.id.not-exist",
                            new Object[] { followerId }, LocaleContextHolder.getLocale()));

        if (!userRepository.existsById(userId))
            throw new ApplicationException(HttpStatus.BAD_REQUEST,
                    messageSource.getMessage("exception.user.id.not-exist",
                            new Object[] { userId }, LocaleContextHolder.getLocale()));

        var follow = followRepository.findByFollowerIdAndUserId(followerId, userId);
        return followMapper.toFollowDto(follow);
    }

    @Override
    public FollowDto follow(FollowDto followDto) {
        if (followDto.getSnippet().getUserId().equals(followDto.getFollowerSnippet().getUserId())) {
            throw new ApplicationException(HttpStatus.BAD_REQUEST,
                    messageSource.getMessage("exception.follow.self-follow",
                            null, LocaleContextHolder.getLocale()));
        }

        if (!userRepository.existsById(followDto.getSnippet().getUserId()))
            throw new ApplicationException(HttpStatus.BAD_REQUEST,
                    messageSource.getMessage("exception.user.id.not-exist",
                            new Object[] { followDto.getSnippet().getUserId() }, LocaleContextHolder.getLocale()));

        if (!userRepository.existsById(followDto.getFollowerSnippet().getUserId()))
            throw new ApplicationException(HttpStatus.BAD_REQUEST,
                    messageSource.getMessage("exception.user.id.not-exist",
                            new Object[] { followDto.getFollowerSnippet().getUserId() },
                            LocaleContextHolder.getLocale()));

        if (followRepository.existsByUserIdAndFollowerId(
                followDto.getSnippet().getUserId(),
                followDto.getFollowerSnippet().getUserId())
        ) {
            throw new ApplicationException(HttpStatus.BAD_REQUEST,
                    messageSource.getMessage("exception.follow.already-exist",
                            null, LocaleContextHolder.getLocale()));
        }

        var follow = followMapper.toFollow(followDto);
        follow.setPublishedAt(LocalDateTime.now());
        followRepository.save(follow);
        return followMapper.toFollowDto(follow);
    }

    @Override
    @Transactional(readOnly = true)
    public List<String> getBrowseKeywords(UUID userId) {
        // TODO
        return hashtagRepository.findAllByUserId(userId).stream()
                .map(Hashtag::getTag)
                .collect(Collectors.toList());
    }
}