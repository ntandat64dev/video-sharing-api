package com.example.videosharingapi.config.aop;

import com.example.videosharingapi.model.entity.Comment;
import com.example.videosharingapi.model.entity.VideoLike;
import com.example.videosharingapi.model.entity.VideoSpec;
import com.example.videosharingapi.model.entity.ViewHistory;
import com.example.videosharingapi.repository.CommentRepository;
import com.example.videosharingapi.repository.VideoLikeRepository;
import com.example.videosharingapi.repository.VideoSpecRepository;
import com.example.videosharingapi.repository.ViewHistoryRepository;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

/**
 * Automatically update {@link VideoSpec} when data in {@link ViewHistoryRepository},
 * {@link VideoLikeRepository} and {@link CommentRepository} get updated. This behavior like {@code trigger} in DBMS.
 *
 * @see VideoSpec
 */
@Aspect
@Component
public class UpdateVideoSpecAspect {
    private final VideoSpecRepository videoSpecRepository;

    public UpdateVideoSpecAspect(VideoSpecRepository videoSpecRepository) {
        this.videoSpecRepository = videoSpecRepository;
    }

    @AfterReturning("(execution(* com.example.videosharingapi.repository.ViewHistoryRepository.save(*)) && args(viewHistory)) || " +
            "(execution(* com.example.videosharingapi.repository.ViewHistoryRepository.saveAndFlush(*)) && args(viewHistory))")
    protected void updateVideoSpecViewCount(ViewHistory viewHistory) {
        videoSpecRepository.findById(viewHistory.getVideo().getId())
                .ifPresent(videoSpec -> videoSpec.setViewCount(videoSpec.getViewCount() + 1));
    }

    @AfterReturning("(execution(* com.example.videosharingapi.repository.VideoLikeRepository.save(*)) && args(videoLike)) || " +
            "(execution(* com.example.videosharingapi.repository.VideoLikeRepository.saveAndFlush(*)) && args(videoLike))")
    protected void updateVideoSpecLikeCount(VideoLike videoLike) {
        videoSpecRepository.findById(videoLike.getVideo().getId())
                .ifPresent(videoSpec -> {
                    if (videoLike.getIsLike())
                        videoSpec.setLikeCount(videoSpec.getLikeCount() + 1);
                    else
                        videoSpec.setDislikeCount(videoSpec.getDislikeCount() + 1);
                });
    }

    @AfterReturning("(execution(* com.example.videosharingapi.repository.CommentRepository.save(*)) && args(comment)) || " +
            "(execution(* com.example.videosharingapi.repository.CommentRepository.saveAndFlush(*)) && args(comment))")
    protected void updateVideoSpecCommentCount(Comment comment) {
        videoSpecRepository.findById(comment.getVideo().getId())
                .ifPresent(videoSpec -> videoSpec.setCommentCount(videoSpec.getCommentCount() + 1));
    }
}