package com.example.videosharingapi.mapper;

import com.example.videosharingapi.dto.VideoDto;
import com.example.videosharingapi.model.entity.User;
import com.example.videosharingapi.model.entity.Video;
import com.example.videosharingapi.model.entity.VideoStatistic;
import org.mapstruct.Builder;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.context.i18n.LocaleContextHolder;

@Mapper(componentModel = "spring",
        imports = { User.class, VideoStatistic.class, LocaleContextHolder.class },
        builder = @Builder(disableBuilder = true),
        uses = { HashtagMapper.class, PrivacyMapper.class, ThumbnailMapper.class, UserMapper.class })
public abstract class VideoMapper {

    @Mapping(target = ".", source = "snippet")
    @Mapping(target = ".", source = "status")
    @Mapping(target = ".", source = "statistic")
    @Mapping(target = "user", source = "snippet.userId")
    @Mapping(target = "videoStatistic", expression = "java(new VideoStatistic())")
    @Mapping(target = "durationSec",
            expression = "java(Math.toIntExact(videoDto.getSnippet().getDuration().toSeconds()))")
    @Mapping(target = "publishedAt", expression = "java(LocalDateTime.now())")
    @Mapping(target = "hashtags", ignore = true)
    public abstract Video toVideo(VideoDto videoDto);

    @Mapping(target = "snippet", expression = "java(mapSnippet(video))")
    @Mapping(target = "status", expression = "java(mapStatus(video))")
    @Mapping(target = "statistic", expression = "java(mapStatistic(video))")
    public abstract VideoDto toVideoDto(Video video);

    @Mapping(target = "duration", expression = "java(Duration.ofSeconds(video.getDurationSec()))")
    @Mapping(target = "userId", source = "user.id")
    @Mapping(target = "username", source = "user.username")
    @Mapping(target = "userImageUrl", source = "user.thumbnails", qualifiedByName = "getDefaultUrl")
    protected abstract VideoDto.Snippet mapSnippet(Video video);

    @Mapping(target = "privacy", source = "privacy.status")
    protected abstract VideoDto.Status mapStatus(Video video);

    @Mapping(target = ".", source = "videoStatistic")
    protected abstract VideoDto.Statistic mapStatistic(Video video);
}
