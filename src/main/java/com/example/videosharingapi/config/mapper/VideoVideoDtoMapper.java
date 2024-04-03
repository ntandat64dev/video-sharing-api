package com.example.videosharingapi.config.mapper;

import com.example.videosharingapi.model.entity.*;
import com.example.videosharingapi.payload.ChannelDto;
import com.example.videosharingapi.payload.VideoDto;
import com.example.videosharingapi.repository.ChannelRepository;
import com.example.videosharingapi.repository.VisibilityRepository;
import org.mapstruct.Builder;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;

import java.util.UUID;

@Mapper(componentModel = "spring",
        imports = { User.class, VideoSpec.class, LocaleContextHolder.class },
        builder = @Builder(disableBuilder = true))
public abstract class VideoVideoDtoMapper {

    @Autowired
    private VisibilityRepository visibilityRepository;

    @Autowired
    private ChannelRepository channelRepository;

    @Mapping(target = "user", expression = "java(User.builder().id(videoDto.getUserId()).build())")
    @Mapping(target = "videoHashtags", ignore = true)
    @Mapping(target = "videoSpec", expression = "java(new VideoSpec())")
    @Mapping(target = "uploadDate", defaultExpression = "java(LocalDateTime.now())")
    public abstract Video videoDtoToVideo(VideoDto videoDto);

    @Mapping(target = "hashtags", source = "videoHashtags")
    @Mapping(target = "userId", expression = "java(video.getUser().getId())")
    @Mapping(target = "visibility", expression = "java(video.getVisibility().getLevel().toString().toLowerCase(LocaleContextHolder.getLocale()))")
    @Mapping(target = "spec", source = "videoSpec")
    @Mapping(target = "channel", expression = "java(findChannel(video.getUser().getId()))")
    public abstract VideoDto videoToVideoDto(Video video);

    protected Visibility visibilityStringToVisibility(String visibilityString) {
        var visibilityLevel = Visibility.VisibilityLevel.valueOf(visibilityString.toUpperCase());
        return visibilityRepository.findByLevel(visibilityLevel);
    }

    protected String videoHashtagsToTags(VideoHashtag videoHashtag) {
        return videoHashtag.getHashtag().getTag();
    }

    protected ChannelDto findChannel(UUID userId) {
        var channel = channelRepository.findByUserId(userId);
        return Mappers.getMapper(ChannelChannelDtoMapper.class).channelToChannelDto(channel);
    }
}
