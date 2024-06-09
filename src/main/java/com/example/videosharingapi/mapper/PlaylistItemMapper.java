package com.example.videosharingapi.mapper;

import com.example.videosharingapi.dto.PlaylistItemDto;
import com.example.videosharingapi.entity.PlaylistItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.time.Duration;

@Mapper(componentModel = "spring",
        imports = Duration.class,
        uses = ThumbnailMapper.class)
public interface PlaylistItemMapper {

    @Mapping(target = "snippet", expression = "java(mapSnippet(playlistItem))")
    @Mapping(target = "contentDetails", expression = "java(mapContentDetails(playlistItem))")
    PlaylistItemDto toPlaylistItemDto(PlaylistItem playlistItem);

    @Mapping(target = "playlistId", source = "playlist.id")
    @Mapping(target = "videoId", source = "video.id")
    @Mapping(target = "title", source = "video.title")
    @Mapping(target = "description", source = "video.description")
    @Mapping(target = "videoUrl", source = "video.videoUrl")
    @Mapping(target = "videoOwnerUsername", source = "video.user.username")
    @Mapping(target = "videoOwnerUserId", source = "video.user.id")
    @Mapping(target = "thumbnails", source = "video.thumbnails")
    PlaylistItemDto.Snippet mapSnippet(PlaylistItem playlistItem);

    @Mapping(target = "duration", expression = "java(Duration.ofSeconds(playlistItem.getVideo().getDurationSec()))")
    PlaylistItemDto.ContentDetails mapContentDetails(PlaylistItem playlistItem);
}
