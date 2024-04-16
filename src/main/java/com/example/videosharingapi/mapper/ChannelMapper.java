package com.example.videosharingapi.mapper;

import com.example.videosharingapi.model.entity.Channel;
import com.example.videosharingapi.dto.ChannelDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = ThumbnailMapper.class)
public interface ChannelMapper {

    @Mapping(target = "snippet", expression = "java(mapSnippet(channel))")
    ChannelDto toChannelDto(Channel channel);

    ChannelDto.Snippet mapSnippet(Channel channel);
}
