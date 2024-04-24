package com.example.videosharingapi.mapper;

import com.example.videosharingapi.dto.FollowDto;
import com.example.videosharingapi.model.entity.Follow;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = { ThumbnailMapper.class, UserMapper.class })
public abstract class FollowMapper {

    @Mapping(target = "user", source = "snippet.userId")
    @Mapping(target = "follower", source = "followerSnippet.userId")
    @Mapping(target = ".", source = "snippet")
    public abstract Follow toFollow(FollowDto followDto);

    @Mapping(target = "snippet", expression = "java(mapSnippet(follow))")
    @Mapping(target = "followerSnippet", expression = "java(mapFollowerSnippet(follow))")
    public abstract FollowDto toFollowDto(Follow follow);

    @Mapping(target = "userId", source = "user.id")
    @Mapping(target = "username", source = "user.username")
    @Mapping(target = "thumbnails", source = "user.thumbnails")
    public abstract FollowDto.Snippet mapSnippet(Follow follow);

    @Mapping(target = "userId", source = "follower.id")
    @Mapping(target = "thumbnails", source = "follower.thumbnails")
    public abstract FollowDto.FollowerSnippet mapFollowerSnippet(Follow follow);
}
