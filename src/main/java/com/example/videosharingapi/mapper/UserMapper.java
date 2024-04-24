package com.example.videosharingapi.mapper;

import com.example.videosharingapi.dto.UserDto;
import com.example.videosharingapi.model.entity.User;
import com.example.videosharingapi.repository.FollowRepository;
import com.example.videosharingapi.repository.UserRepository;
import com.example.videosharingapi.repository.VideoRepository;
import com.example.videosharingapi.repository.VideoStatisticRepository;
import org.mapstruct.Builder;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.UUID;

@Mapper(componentModel = "spring", builder = @Builder(disableBuilder = true), uses = ThumbnailMapper.class)
@SuppressWarnings("SpringJavaAutowiredFieldsWarningInspection")
public abstract class UserMapper {

    private @Autowired UserRepository userRepository;
    private @Autowired VideoStatisticRepository videoStatisticRepository;
    private @Autowired VideoRepository videoRepository;
    private @Autowired FollowRepository followRepository;

    @Mapping(target = "snippet", expression = "java(mapSnippet(user))")
    @Mapping(target = "statistic", expression = "java(mapStatistic(user))")
    public abstract UserDto toUserDto(User user);

    protected abstract UserDto.Snippet mapSnippet(User user);

    protected UserDto.Statistic mapStatistic(User user) {
        var statistic = new UserDto.Statistic();
        var viewCount = videoStatisticRepository.sumViewCountByUserId(user.getId());
        var videoCount = videoRepository.countByUserId(user.getId());
        var followerCount = followRepository.countByUserId(user.getId());
        var followingCount = followRepository.countByFollowerId(user.getId());
        statistic.setViewCount(viewCount);
        statistic.setVideoCount(videoCount);
        statistic.setFollowerCount(followerCount);
        statistic.setFollowingCount(followingCount);
        return statistic;
    }

    public User findUser(UUID userId) {
        return userRepository.getReferenceById(userId);
    }
}
