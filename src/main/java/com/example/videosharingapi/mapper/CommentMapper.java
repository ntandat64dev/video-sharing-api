package com.example.videosharingapi.mapper;

import com.example.videosharingapi.model.entity.Comment;
import com.example.videosharingapi.model.entity.CommentRating;
import com.example.videosharingapi.dto.CommentDto;
import com.example.videosharingapi.repository.CommentRatingRepository;
import com.example.videosharingapi.repository.CommentRepository;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.beans.factory.annotation.Autowired;

@Mapper(componentModel = "spring", uses = ThumbnailMapper.class)
@SuppressWarnings("SpringJavaAutowiredFieldsWarningInspection")
public abstract class CommentMapper {

    private @Autowired CommentRatingRepository commentRatingRepository;
    private @Autowired CommentRepository commentRepository;

    @Mapping(target = "snippet", expression = "java(mapSnippet(comment))")
    @Mapping(target = "statistic", expression = "java(mapStatistic(comment))")
    public abstract CommentDto toCommentDto(Comment comment);

    @Mapping(target = "videoId", source = "video.id")
    @Mapping(target = "authorId", source = "user.id")
    @Mapping(target = "authorDisplayName", source = "user.channel.title")
    @Mapping(target = "authorProfileImageUrl", source = "user.channel.thumbnails", qualifiedByName = "defaultUrl")
    @Mapping(target = "parentId", source = "parent.id")
    public abstract CommentDto.Snippet mapSnippet(Comment comment);

    @Mapping(target = "likeCount", expression = "java(getLikeCount(comment))")
    @Mapping(target = "dislikeCount", expression = "java(getDislikeCount(comment))")
    @Mapping(target = "replyCount", expression = "java(getReplyCount(comment))")
    public abstract CommentDto.Statistic mapStatistic(Comment comment);

    protected Integer getLikeCount(Comment comment) {
        return Math.toIntExact(commentRatingRepository.countByCommentIdAndRating(comment.getId(),
                CommentRating.Rating.LIKE));
    }

    protected Integer getDislikeCount(Comment comment) {
        return Math.toIntExact(commentRatingRepository.countByCommentIdAndRating(comment.getId(),
                CommentRating.Rating.DISLIKE));
    }

    protected Integer getReplyCount(Comment comment) {
        return Math.toIntExact(commentRepository.countByParentId(comment.getId()));
    }
}
