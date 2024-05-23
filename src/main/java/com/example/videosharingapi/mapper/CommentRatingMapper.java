package com.example.videosharingapi.mapper;

import com.example.videosharingapi.dto.CommentRatingDto;
import com.example.videosharingapi.entity.CommentRating;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CommentRatingMapper {

    @Mapping(target = "commentId", source = "comment.id")
    @Mapping(target = "userId", source = "user.id")
    @Mapping(target = "rating", expression = "java(commentRating.getRating().name().toLowerCase())")
    CommentRatingDto toCommentRatingDto(CommentRating commentRating);

    default CommentRatingDto createNoneRatingDto(String commentId, String userId) {
        var dto = new CommentRatingDto();
        dto.setCommentId(commentId);
        dto.setUserId(userId);
        dto.setRating(CommentRatingDto.NONE);
        return dto;
    }
}
