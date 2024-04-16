package com.example.videosharingapi.dto;

import com.example.videosharingapi.model.entity.Thumbnail;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@Getter
@Setter
public final class ChannelDto {

    @Getter
    @Setter
    public static final class Snippet {

        private String title;

        private String description;

        private LocalDateTime publishedAt;

        private Map<Thumbnail.Type, ThumbnailDto> thumbnails;
    }

    private UUID id;

    private Snippet snippet;
}