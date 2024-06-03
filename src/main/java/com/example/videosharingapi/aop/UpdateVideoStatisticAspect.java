package com.example.videosharingapi.aop;

import com.example.videosharingapi.entity.Comment;
import com.example.videosharingapi.entity.VideoRating;
import com.example.videosharingapi.entity.VideoStatistic;
import com.example.videosharingapi.entity.ViewHistory;
import com.example.videosharingapi.repository.CommentRepository;
import com.example.videosharingapi.repository.VideoRatingRepository;
import com.example.videosharingapi.repository.VideoStatisticRepository;
import com.example.videosharingapi.repository.ViewHistoryRepository;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * Automatically update {@link VideoStatistic} when data in {@link ViewHistoryRepository},
 * {@link VideoRatingRepository} and {@link CommentRepository} get updated. This behavior like {@code trigger} in DBMS.
 *
 * @see VideoStatistic
 */
@Aspect
@Component
@Transactional
@RequiredArgsConstructor
public class UpdateVideoStatisticAspect {
    private final VideoStatisticRepository videoStatisticRepository;
    private final VideoRatingRepository videoRatingRepository;

    /**
     * Increase {@code VideoStatistic.viewCount} by 1 if {@link ViewHistory} is not updating.
     */
    @Around("""
            (execution(* com.example.videosharingapi.repository.ViewHistoryRepository.save(*)) ||
            execution(* com.example.videosharingapi.repository.ViewHistoryRepository.saveAndFlush(*))) &&
            args(viewHistory)""")
    public Object updateViewCount(ProceedingJoinPoint joinPoint, ViewHistory viewHistory) throws Throwable {
        // If the saveVideo*() method is used for updating the ViewHistory, then do nothing and return.
        if (viewHistory.getId() != null) return joinPoint.proceed();
        var result = joinPoint.proceed();

        // Else increase video statistic's viewCount by 1.
        videoStatisticRepository
                .findById(viewHistory.getVideo().getId())
                .ifPresent(videoStat -> videoStat.setViewCount(videoStat.getViewCount() + 1));

        return result;
    }

    @Around("""
            execution(* com.example.videosharingapi.repository.VideoRatingRepository.save(*)) ||
            execution(* com.example.videosharingapi.repository.VideoRatingRepository.saveAndFlush(*))""")
    public Object updateLikeCountWhenSave(ProceedingJoinPoint joinPoint) throws Throwable {
        var videoRating = (VideoRating) joinPoint.getArgs()[0];
        var ratedBefore = videoRatingRepository.existsByUserIdAndVideoId(
                videoRating.getUser().getId(),
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

    @Around("""
            execution(* com.example.videosharingapi.repository.CommentRepository.deleteById(*)) &&
            args(commentId)""")
    public Object updateCommentCountWhenDeleteComment(
            ProceedingJoinPoint joinPoint, String commentId
    ) throws Throwable {
        var videoStat = videoStatisticRepository.findByCommentId(commentId);
        var result = joinPoint.proceed();
        videoStat.setCommentCount(videoStat.getCommentCount() - 1);
        return result;
    }

    @Around("""
            execution(* com.example.videosharingapi.repository.CommentRepository.deleteByParentId(*)) &&
            args(commentId)""")
    public Object updateCommentCountWhenDeleteReply(
            ProceedingJoinPoint joinPoint, String commentId
    ) throws Throwable {
        var videoStat = videoStatisticRepository.findByCommentId(commentId);
        var rows = (long) joinPoint.proceed();
        videoStat.setCommentCount(videoStat.getCommentCount() - rows);
        return rows;
    }
}