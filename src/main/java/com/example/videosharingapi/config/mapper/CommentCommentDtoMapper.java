package com.example.videosharingapi.config.mapper;

import com.example.videosharingapi.model.entity.Comment;
import com.example.videosharingapi.payload.CommentDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CommentCommentDtoMapper {

    @Mapping(target = "parentId", expression = "java(comment.getParent() != null ? comment.getParent().getId() : null)")
    @Mapping(target = "commentedBy", expression = "java(comment.getUser().getId())")
    @Mapping(target = "videoId", expression = "java(comment.getVideo().getId())")
    CommentDto commentToCommentDto(Comment comment);
}
