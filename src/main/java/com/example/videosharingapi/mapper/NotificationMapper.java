package com.example.videosharingapi.mapper;

import com.example.videosharingapi.dto.NotificationDto;
import com.example.videosharingapi.dto.ThumbnailDto;
import com.example.videosharingapi.entity.Notification;
import com.example.videosharingapi.entity.Thumbnail;
import com.example.videosharingapi.repository.VideoRepository;
import lombok.Setter;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashMap;
import java.util.Map;

@Mapper(componentModel = "spring", uses = ThumbnailMapper.class)
@Setter(onMethod_ = @Autowired)
public abstract class NotificationMapper {
    private VideoRepository videoRepository;
    private ThumbnailMapper thumbnailMapper;

    @Mapping(target = "snippet", expression = "java(mapSnippet(notification))")
    public abstract NotificationDto toNotificationDto(Notification notification);

    @Mapping(target = "actorId", source = "actor.id")
    @Mapping(target = "actorImageUrl", source = "notification.actor.thumbnails", qualifiedByName = "getDefaultUrl")
    @Mapping(target = "recipientId", source = "recipient.id")
    @Mapping(target = "thumbnails", expression = "java(mapThumbnails(notification))")
    @Mapping(target = ".", source = "notificationObject")
    public abstract NotificationDto.Snippet mapSnippet(Notification notification);

    protected Map<Thumbnail.Type, ThumbnailDto> mapThumbnails(Notification notification) {
        var objectType = notification.getNotificationObject().getObjectType();
        var objectId = notification.getNotificationObject().getObjectId();
        return switch (objectType) {
            case VIDEO, COMMENT -> {
                var video = videoRepository.findById(objectId).orElseThrow();
                yield thumbnailMapper.toMap(video.getThumbnails());
            }
            default -> new HashMap<>();
        };
    }
}