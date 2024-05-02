package com.example.videosharingapi.mapper;

import com.example.videosharingapi.dto.CommentDto;
import com.example.videosharingapi.entity.Comment;
import com.example.videosharingapi.entity.CommentRating;
import com.example.videosharingapi.entity.User;
import com.example.videosharingapi.entity.Video;
import com.example.videosharingapi.repository.CommentRatingRepository;
import com.example.videosharingapi.repository.CommentRepository;
import com.example.videosharingapi.repository.UserRepository;
import com.example.videosharingapi.repository.VideoRepository;
import lombok.Setter;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.beans.factory.annotation.Autowired;

@Mapper(componentModel = "spring", uses = ThumbnailMapper.class)
@Setter(onMethod_ = @Autowired)
public abstract class CommentMapper {

    private CommentRatingRepository commentRatingRepository;
    private CommentRepository commentRepository;
    private UserRepository userRepository;
    private VideoRepository videoRepository;

    @Mapping(target = ".", source = "snippet")
    @Mapping(target = "publishedAt", expression = "java(LocalDateTime.now())")
    @Mapping(target = "parent", expression = "java(findParent(commentDto))")
    @Mapping(target = "user", expression = "java(findUser(commentDto))")
    @Mapping(target = "video", expression = "java(findVideo(commentDto))")
    public abstract Comment toComment(CommentDto commentDto);

    protected Comment findParent(CommentDto commentDto) {
        if (commentDto.getSnippet().getParentId() == null) return null;
        return commentRepository.getReferenceById(commentDto.getSnippet().getParentId());
    }

    protected User findUser(CommentDto commentDto) {
        return userRepository.getReferenceById(commentDto.getSnippet().getAuthorId());
    }

    protected Video findVideo(CommentDto commentDto) {
        return videoRepository.getReferenceById(commentDto.getSnippet().getVideoId());
    }

    @Mapping(target = "snippet", expression = "java(mapSnippet(comment))")
    @Mapping(target = "statistic", expression = "java(mapStatistic(comment))")
    public abstract CommentDto toCommentDto(Comment comment);

    @Mapping(target = "videoId", source = "video.id")
    @Mapping(target = "authorId", source = "user.id")
    @Mapping(target = "authorDisplayName", source = "user.username")
    @Mapping(target = "authorProfileImageUrl", source = "user.thumbnails", qualifiedByName = "getDefaultUrl")
    @Mapping(target = "parentId", source = "parent.id")
    public abstract CommentDto.Snippet mapSnippet(Comment comment);

    @Mapping(target = "likeCount", expression = "java(getLikeCount(comment))")
    @Mapping(target = "dislikeCount", expression = "java(getDislikeCount(comment))")
    @Mapping(target = "replyCount", expression = "java(getReplyCount(comment))")
    public abstract CommentDto.Statistic mapStatistic(Comment comment);

    protected Long getLikeCount(Comment comment) {
        return commentRatingRepository.countByCommentIdAndRating(comment.getId(), CommentRating.Rating.LIKE);
    }

    protected Long getDislikeCount(Comment comment) {
        return commentRatingRepository.countByCommentIdAndRating(comment.getId(), CommentRating.Rating.DISLIKE);
    }

    protected Long getReplyCount(Comment comment) {
        return commentRepository.countByParentId(comment.getId());
    }
}
