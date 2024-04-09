package com.example.videosharingapi.config.aop;

import com.example.videosharingapi.model.entity.Comment;
import com.example.videosharingapi.model.entity.VideoRating;
import com.example.videosharingapi.model.entity.VideoSpec;
import com.example.videosharingapi.model.entity.ViewHistory;
import com.example.videosharingapi.repository.CommentRepository;
import com.example.videosharingapi.repository.VideoRatingRepository;
import com.example.videosharingapi.repository.VideoSpecRepository;
import com.example.videosharingapi.repository.ViewHistoryRepository;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * Automatically update {@link VideoSpec} when data in {@link ViewHistoryRepository},
 * {@link VideoRatingRepository} and {@link CommentRepository} get updated. This behavior like {@code trigger} in DBMS.
 *
 * @see VideoSpec
 */
@Aspect
@Component
@Transactional
public class UpdateVideoSpecAspect {
    private final VideoSpecRepository videoSpecRepository;
    private final VideoRatingRepository videoRatingRepository;

    public UpdateVideoSpecAspect(VideoSpecRepository videoSpecRepository, VideoRatingRepository videoRatingRepository) {
        this.videoSpecRepository = videoSpecRepository;
        this.videoRatingRepository = videoRatingRepository;
    }

    /**
     * Increase {@code VideoSpec.viewCount} by 1 if {@link ViewHistory} is not updating.
     */
    @Around("execution(* com.example.videosharingapi.repository.ViewHistoryRepository.save(*)) || " +
            "execution(* com.example.videosharingapi.repository.ViewHistoryRepository.saveAndFlush(*))")
    public Object updateVideoSpecViewCount(ProceedingJoinPoint joinPoint) throws Throwable {
        var viewHistory = (ViewHistory) joinPoint.getArgs()[0];
        // If the saveVideo*() method is used for updating the ViewHistory.
        if (viewHistory.getId() != null) return joinPoint.proceed();
        var result = joinPoint.proceed();
        videoSpecRepository
                .findById(viewHistory.getVideo().getId())
                .ifPresent(videoSpec -> videoSpec.setViewCount(videoSpec.getViewCount() + 1));
        return result;
    }

    @Around("execution(* com.example.videosharingapi.repository.VideoRatingRepository.save(*)) || " +
            "execution(* com.example.videosharingapi.repository.VideoRatingRepository.saveAndFlush(*))")
    public Object updateVideoSpecLikeCountWhenSave(ProceedingJoinPoint joinPoint) throws Throwable {
        var videoRating = (VideoRating) joinPoint.getArgs()[0];
        var ratedBefore = videoRatingRepository.existsByUserIdAndVideoId(videoRating.getUser().getId(), videoRating.getVideo().getId());
        var result = joinPoint.proceed();
        videoSpecRepository.findById(videoRating.getVideo().getId()).ifPresent(videoSpec -> {
            if (videoRating.getRating() == VideoRating.Rating.LIKE) {
                videoSpec.setLikeCount(videoSpec.getLikeCount() + 1);
                if (ratedBefore) videoSpec.setDislikeCount(videoSpec.getDislikeCount() - 1);
            } else {
                videoSpec.setDislikeCount(videoSpec.getDislikeCount() + 1);
                if (ratedBefore) videoSpec.setLikeCount(videoSpec.getLikeCount() - 1);
            }
        });
        return result;
    }

    @AfterReturning("(execution(* com.example.videosharingapi.repository.VideoRatingRepository.delete(*)) && args(videoRating))")
    public void updateVideoSpecLikeCountWhenDelete(VideoRating videoRating) {
        videoSpecRepository.findById(videoRating.getVideo().getId()).ifPresent(videoSpec -> {
            if (videoRating.getRating() == VideoRating.Rating.LIKE) {
                videoSpec.setLikeCount(videoSpec.getLikeCount() - 1);
            } else {
                videoSpec.setDislikeCount(videoSpec.getDislikeCount() - 1);
            }
        });
    }

    @AfterReturning("(execution(* com.example.videosharingapi.repository.CommentRepository.save(*)) && args(comment)) || " +
            "(execution(* com.example.videosharingapi.repository.CommentRepository.saveAndFlush(*)) && args(comment))")
    public void updateVideoSpecCommentCount(Comment comment) {
        videoSpecRepository.findById(comment.getVideo().getId())
                .ifPresent(videoSpec -> videoSpec.setCommentCount(videoSpec.getCommentCount() + 1));
    }
}