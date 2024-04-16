package com.example.videosharingapi.mapper;

import com.example.videosharingapi.model.entity.User;
import com.example.videosharingapi.model.entity.Video;
import com.example.videosharingapi.model.entity.VideoStatistic;
import com.example.videosharingapi.dto.VideoDto;
import org.mapstruct.Builder;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.context.i18n.LocaleContextHolder;

@Mapper(componentModel = "spring",
        imports = { User.class, VideoStatistic.class, LocaleContextHolder.class },
        builder = @Builder(disableBuilder = true),
        uses = { HashtagMapper.class, PrivacyMapper.class, ThumbnailMapper.class })
public abstract class VideoMapper {

    @Mapping(target = ".", source = "snippet")
    @Mapping(target = ".", source = "status")
    @Mapping(target = ".", source = "statistic")
    @Mapping(target = "user", expression = "java(User.builder().id(videoDto.getSnippet().getUserId()).build())")
    @Mapping(target = "videoStatistic", expression = "java(new VideoStatistic())")
    @Mapping(target = "durationSec",
            expression = "java(Math.toIntExact(videoDto.getSnippet().getDuration().toSeconds()))")
    @Mapping(target = "publishedAt", source = "snippet.publishedAt", defaultExpression = "java(LocalDateTime.now())")
    @Mapping(target = "hashtags", ignore = true)
    public abstract Video toVideo(VideoDto videoDto);

    @Mapping(target = "snippet", expression = "java(mapSnippet(video))")
    @Mapping(target = "status", expression = "java(mapStatus(video))")
    @Mapping(target = "statistic", expression = "java(mapStatistic(video))")
    public abstract VideoDto toVideoDto(Video video);

    @Mapping(target = "duration", expression = "java(Duration.ofSeconds(video.getDurationSec()))")
    @Mapping(target = "userId", source = "user.id")
    @Mapping(target = "channelTitle", source = "user.channel.title")
    @Mapping(target = "channelImageUrl", source = "user.channel.thumbnails", qualifiedByName = "defaultUrl")
    protected abstract VideoDto.Snippet mapSnippet(Video video);

    @Mapping(target = "privacy", source = "privacy.status")
    protected abstract VideoDto.Status mapStatus(Video video);

    @Mapping(target = ".", source = "videoStatistic")
    protected abstract VideoDto.Statistic mapStatistic(Video video);
}
