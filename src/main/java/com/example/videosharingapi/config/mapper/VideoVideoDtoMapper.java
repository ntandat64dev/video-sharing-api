package com.example.videosharingapi.config.mapper;

import com.example.videosharingapi.model.entity.*;
import com.example.videosharingapi.payload.ChannelDto;
import com.example.videosharingapi.payload.VideoDto;
import com.example.videosharingapi.repository.ChannelRepository;
import com.example.videosharingapi.repository.HashtagRepository;
import com.example.videosharingapi.repository.VisibilityRepository;
import jakarta.annotation.Nullable;
import org.mapstruct.Builder;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;

import java.util.List;
import java.util.Set;
import java.util.UUID;

@Mapper(componentModel = "spring",
        imports = { User.class, VideoSpec.class, LocaleContextHolder.class },
        builder = @Builder(disableBuilder = true))
public abstract class VideoVideoDtoMapper {

    private @Autowired VisibilityRepository visibilityRepository;
    private @Autowired ChannelRepository channelRepository;
    private @Autowired HashtagRepository hashtagRepository;

    @Mapping(target = "user", expression = "java(User.builder().id(videoDto.getUserId()).build())")
    @Mapping(target = "videoSpec", expression = "java(new VideoSpec())")
    @Mapping(target = "uploadDate", defaultExpression = "java(LocalDateTime.now())")
    @Mapping(target = "hashtags", expression = "java(toHashtags(videoDto.getHashtags()))")
    public abstract Video videoDtoToVideo(VideoDto videoDto);

    @Mapping(target = "userId", expression = "java(video.getUser().getId())")
    @Mapping(target = "visibility", expression = "java(video.getVisibility().getLevel().toString()" +
            ".toLowerCase(LocaleContextHolder.getLocale()))")
    @Mapping(target = "spec", source = "videoSpec")
    @Mapping(target = "channel", expression = "java(findChannel(video.getUser().getId()))")
    public abstract VideoDto videoToVideoDto(Video video);

    protected Visibility visibilityStringToVisibility(String visibilityString) {
        var visibilityLevel = Visibility.VisibilityLevel.valueOf(visibilityString.toUpperCase());
        return visibilityRepository.findByLevel(visibilityLevel);
    }

    protected String hashtagToString(Hashtag hashtag) {
        return hashtag.getTag();
    }

    protected List<Hashtag> toHashtags(@Nullable Set<String> hashtags) {
        if (hashtags == null) return null;
        return hashtags.stream()
                .map(Hashtag::new)
                .map(hashtagRepository::saveIfAbsent)
                .toList();
    }

    protected ChannelDto findChannel(UUID userId) {
        var channel = channelRepository.findByUserId(userId);
        return Mappers.getMapper(ChannelChannelDtoMapper.class).channelToChannelDto(channel);
    }
}
