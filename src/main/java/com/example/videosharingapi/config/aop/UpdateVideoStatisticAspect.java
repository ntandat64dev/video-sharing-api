package com.example.videosharingapi.config.aop;

import com.example.videosharingapi.model.entity.Comment;
import com.example.videosharingapi.model.entity.VideoRating;
import com.example.videosharingapi.model.entity.VideoStatistic;
import com.example.videosharingapi.model.entity.ViewHistory;
import com.example.videosharingapi.repository.CommentRepository;
import com.example.videosharingapi.repository.VideoRatingRepository;
import com.example.videosharingapi.repository.VideoStatisticRepository;
import com.example.videosharingapi.repository.ViewHistoryRepository;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

// TODO: Update this class
// TODO: Test getTopLevelComment
// TODO: Update data-h2.sql
// TODO: Update validation message
// TODO: Refactor test method name

/**
 * Automatically update {@link VideoStatistic} when data in {@link ViewHistoryRepository},
 * {@link VideoRatingRepository} and {@link CommentRepository} get updated. This behavior like {@code trigger} in DBMS.
 *
 * @see VideoStatistic
 */
@Aspect
@Component
@Transactional
public class UpdateVideoStatisticAspect {
    private final VideoStatisticRepository videoStatisticRepository;
    private final VideoRatingRepository videoRatingRepository;

    public UpdateVideoStatisticAspect(VideoStatisticRepository videoStatisticRepository,
                                      VideoRatingRepository videoRatingRepository) {
        this.videoStatisticRepository = videoStatisticRepository;
        this.videoRatingRepository = videoRatingRepository;
    }

    /**
     * Increase {@code VideoStatistic.viewCount} by 1 if {@link ViewHistory} is not updating.
     */
    @Around("""
            execution(* com.example.videosharingapi.repository.ViewHistoryRepository.save(*)) ||
            execution(* com.example.videosharingapi.repository.ViewHistoryRepository.saveAndFlush(*))""")
    public Object updateViewCount(ProceedingJoinPoint joinPoint) throws Throwable {
        var viewHistory = (ViewHistory) joinPoint.getArgs()[0];
        // If the saveVideo*() method is used for updating the ViewHistory.
        if (viewHistory.getId() != null) return joinPoint.proceed();
        var result = joinPoint.proceed();
        videoStatisticRepository
                .findById(viewHistory.getVideo().getId())
                .ifPresent(stat -> stat.setViewCount(stat.getViewCount() + 1));
        return result;
    }

    @Around("""
            execution(* com.example.videosharingapi.repository.VideoRatingRepository.save(*)) ||
            execution(* com.example.videosharingapi.repository.VideoRatingRepository.saveAndFlush(*))""")
    public Object updateLikeCountWhenSave(ProceedingJoinPoint joinPoint) throws Throwable {
        var videoRating = (VideoRating) joinPoint.getArgs()[0];
        var ratedBefore = videoRatingRepository.existsByUserIdAndVideoId(videoRating.getUser().getId(),
                videoRating.getVideo().getId());
        var result = joinPoint.proceed();
        videoStatisticRepository.findById(videoRating.getVideo().getId()).ifPresent(stat -> {
            if (videoRating.getRating() == VideoRating.Rating.LIKE) {
                stat.setLikeCount(stat.getLikeCount() + 1);
                if (ratedBefore) stat.setDislikeCount(stat.getDislikeCount() - 1);
            } else {
                stat.setDislikeCount(stat.getDislikeCount() + 1);
                if (ratedBefore) stat.setLikeCount(stat.getLikeCount() - 1);
            }
        });
        return result;
    }

    @AfterReturning("""
            execution(* com.example.videosharingapi.repository.VideoRatingRepository.delete(*)) &&
            args(videoRating)""")
    public void updateLikeCountWhenDelete(VideoRating videoRating) {
        videoStatisticRepository.findById(videoRating.getVideo().getId()).ifPresent(stat -> {
            if (videoRating.getRating() == VideoRating.Rating.LIKE) {
                stat.setLikeCount(stat.getLikeCount() - 1);
            } else {
                stat.setDislikeCount(stat.getDislikeCount() - 1);
            }
        });
    }

    @AfterReturning("""
            (execution(* com.example.videosharingapi.repository.CommentRepository.save(*)) ||
            execution(* com.example.videosharingapi.repository.CommentRepository.saveAndFlush(*))) &&
            args(comment)""")
    public void updateCommentCount(Comment comment) {
        videoStatisticRepository.findById(comment.getVideo().getId())
                .ifPresent(stat -> stat.setCommentCount(stat.getCommentCount() + 1));
    }
}