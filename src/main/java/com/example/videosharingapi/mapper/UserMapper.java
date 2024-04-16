package com.example.videosharingapi.mapper;

import com.example.videosharingapi.model.entity.User;
import com.example.videosharingapi.dto.UserDto;
import org.mapstruct.Builder;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring",
        builder = @Builder(disableBuilder = true),
        uses = ChannelMapper.class)
public interface UserMapper {

    @Mapping(target = "snippet", expression = "java(mapSnippet(user))")
    UserDto toUserDto(User user);

    UserDto.Snippet mapSnippet(User snippet);
}
