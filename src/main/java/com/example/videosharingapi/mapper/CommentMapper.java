package com.example.videosharingapi.mapper;

import com.example.videosharingapi.dto.CommentDto;
import com.example.videosharingapi.model.entity.Comment;
import com.example.videosharingapi.model.entity.CommentRating;
import com.example.videosharingapi.model.entity.User;
import com.example.videosharingapi.model.entity.Video;
import com.example.videosharingapi.repository.CommentRatingRepository;
import com.example.videosharingapi.repository.CommentRepository;
import com.example.videosharingapi.repository.UserRepository;
import com.example.videosharingapi.repository.VideoRepository;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.beans.factory.annotation.Autowired;

@Mapper(componentModel = "spring", uses = ThumbnailMapper.class)
@SuppressWarnings("SpringJavaAutowiredFieldsWarningInspection")
public abstract class CommentMapper {

    private @Autowired CommentRatingRepository commentRatingRepository;
    private @Autowired CommentRepository commentRepository;
    private @Autowired UserRepository userRepository;
    private @Autowired VideoRepository videoRepository;

    @Mapping(target = ".", source = "snippet")
    @Mapping(target = "publishedAt", source = "snippet.publishedAt", defaultExpression = "java(LocalDateTime.now())")
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
