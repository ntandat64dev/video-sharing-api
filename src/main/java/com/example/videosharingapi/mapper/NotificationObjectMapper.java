package com.example.videosharingapi.mapper;

import com.example.videosharingapi.dto.NotificationDto;
import com.example.videosharingapi.entity.NotificationObject;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.time.LocalDateTime;

@Mapper(componentModel = "spring", imports = LocalDateTime.class)
public abstract class NotificationObjectMapper {

    @Mapping(target = "publishedAt", defaultExpression = "java(LocalDateTime.now())")
    @Mapping(target = "notifications", ignore = true)
    @Mapping(target = "id", ignore = true)
    public abstract NotificationObject fromNotificationDtoSnippet(NotificationDto.Snippet snippet);
}
