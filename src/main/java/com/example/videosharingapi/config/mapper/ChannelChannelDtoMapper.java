package com.example.videosharingapi.config.mapper;

import com.example.videosharingapi.model.entity.Channel;
import com.example.videosharingapi.payload.ChannelDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ChannelChannelDtoMapper {
    ChannelDto channelToChannelDto(Channel channel);
}
