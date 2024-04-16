package com.example.videosharingapi.mapper;

import com.example.videosharingapi.model.entity.Comment;
import com.example.videosharingapi.model.entity.CommentRating;
import com.example.videosharingapi.model.entity.Thumbnail;
import com.example.videosharingapi.payload.CommentDto;
import com.example.videosharingapi.repository.CommentRatingRepository;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.beans.factory.annotation.Autowired;

@Mapper(componentModel = "spring")
@SuppressWarnings("SpringJavaAutowiredFieldsWarningInspection")
public abstract class CommentMapper {

    private @Autowired CommentRatingRepository commentRatingRepository;

    @Mapping(target = "snippet", expression = "java(mapSnippet(comment))")
    public abstract CommentDto toCommentDto(Comment comment);

    @Mapping(target = "videoId", source = "video.id")
    @Mapping(target = "authorId", source = "user.id")
    @Mapping(target = "authorDisplayName", source = "user.channel.title")
    @Mapping(target = "authorProfileImageUrl", expression = "java(mapAuthorProfileImageUrl(comment))")
    @Mapping(target = "parentId", source = "parent.id")
    @Mapping(target = "likeCount", expression = "java(mapLikeCount(comment))")
    @Mapping(target = "dislikeCount", expression = "java(mapDislikeCount(comment))")
    public abstract CommentDto.Snippet mapSnippet(Comment comment);

    protected String mapAuthorProfileImageUrl(Comment comment) {
        return comment.getUser().getChannel().getThumbnails().stream()
                .filter(thumbnail -> thumbnail.getType() == Thumbnail.Type.DEFAULT)
                .findFirst()
                .orElseThrow()
                .getUrl();
    }

    protected Integer mapLikeCount(Comment comment) {
        return Math.toIntExact(commentRatingRepository.countByCommentIdAndRating(comment.getId(),
                CommentRating.Rating.LIKE));
    }

    protected Integer mapDislikeCount(Comment comment) {
        return Math.toIntExact(commentRatingRepository.countByCommentIdAndRating(comment.getId(),
                CommentRating.Rating.DISLIKE));
    }
}
