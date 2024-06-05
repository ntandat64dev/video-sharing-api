package com.example.videosharingapi.aop;

import com.example.videosharingapi.entity.PlaylistItem;
import com.example.videosharingapi.entity.VideoRating;
import com.example.videosharingapi.repository.PlaylistItemRepository;
import com.example.videosharingapi.repository.PlaylistRepository;
import com.example.videosharingapi.repository.VideoRatingRepository;
import com.example.videosharingapi.repository.VideoRepository;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Aspect
@Component
@Transactional
@RequiredArgsConstructor
public class AddLikedVideoToPlaylistAspect {

    private final VideoRepository videoRepository;
    private final VideoRatingRepository videoRatingRepository;
    private final PlaylistRepository playlistRepository;
    private final PlaylistItemRepository playlistItemRepository;

    @AfterReturning("execution(* com.example.videosharingapi.service.VideoService.rateVideo(..))")
    public void afterRateVideo(JoinPoint joinPoint) {
        var videoId = (String) joinPoint.getArgs()[0];
        var userId = (String) joinPoint.getArgs()[1];

        var video = videoRepository.findById(videoId).orElseThrow();
        var playlist = playlistRepository.findLikedVideosPlaylistByUserId(userId);
        var videoRating = videoRatingRepository.findByUserIdAndVideoId(userId, videoId);
        var playlistItem = playlistItemRepository
                .findByPlaylistIdAndPlaylistUserIdAndVideoId(playlist.getId(), userId, videoId);

        if (playlistItem != null && (videoRating == null || videoRating.getRating() == VideoRating.Rating.DISLIKE)) {
            playlistItemRepository.delete(playlistItem);
        } else if (videoRating != null && videoRating.getRating() == VideoRating.Rating.LIKE) {
            var newPlaylistItem = new PlaylistItem();
            newPlaylistItem.setPlaylist(playlist);
            newPlaylistItem.setVideo(video);
            newPlaylistItem.setPriority(playlistItemRepository.getMaxPriorityByPlaylistId(playlist.getId()) + 1);
            playlistItemRepository.save(newPlaylistItem);
        }
    }
}
