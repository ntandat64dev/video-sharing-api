package com.example.videosharingapi.config.mapper;

import com.example.videosharingapi.model.entity.User;
import com.example.videosharingapi.payload.ChannelDto;
import com.example.videosharingapi.payload.UserDto;
import com.example.videosharingapi.repository.ChannelRepository;
import org.mapstruct.Builder;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import org.springframework.beans.factory.annotation.Autowired;

@Mapper(componentModel = "spring", builder = @Builder(disableBuilder = true))
public abstract class UserUserDtoMapper {

    @Autowired
    private ChannelRepository channelRepository;

    @Mapping(target = "channel", expression = "java(findChannel(user))")
    public abstract UserDto userToUserDto(User user);

    protected ChannelDto findChannel(User user) {
        var channel = channelRepository.findByUserId(user.getId());
        return Mappers.getMapper(ChannelChannelDtoMapper.class).channelToChannelDto(channel);
    }
}
