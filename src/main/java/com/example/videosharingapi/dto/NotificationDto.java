package com.example.videosharingapi.dto;

import com.example.videosharingapi.entity.NotificationObject;
import com.example.videosharingapi.entity.Thumbnail;
import jakarta.validation.Valid;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Map;

@Getter
@Setter
public class NotificationDto {

    @Getter
    @Setter
    @Builder
    public static final class Snippet {

        private LocalDateTime publishedAt;

        private Map<Thumbnail.Type, ThumbnailDto> thumbnails;

        private String actorId;

        private String actorImageUrl;

        private String recipientId;

        private String message;

        private Boolean isSeen;

        private Boolean isRead;

        private Integer actionType;

        private NotificationObject.ObjectType objectType;

        private String objectId;
    }

    private String id;

    @Valid
    private Snippet snippet;
}
