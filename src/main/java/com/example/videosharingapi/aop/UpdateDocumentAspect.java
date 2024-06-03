package com.example.videosharingapi.aop;

import com.example.videosharingapi.elasticsearchrepository.PlaylistElasticsearchRepository;
import com.example.videosharingapi.elasticsearchrepository.UserElasticsearchRepository;
import com.example.videosharingapi.elasticsearchrepository.VideoElasticsearchRepository;
import com.example.videosharingapi.entity.*;
import com.example.videosharingapi.repository.VideoStatisticRepository;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Aspect
@Component
@Transactional
@RequiredArgsConstructor
public class UpdateDocumentAspect {
    private final VideoStatisticRepository videoStatisticRepository;

    private final VideoElasticsearchRepository videoElasticsearchRepository;
    private final UserElasticsearchRepository userElasticsearchRepository;
    private final PlaylistElasticsearchRepository playlistElasticsearchRepository;

    @AfterReturning("""
            (execution(* com.example.videosharingapi.repository.VideoRepository.save(*)) ||
            execution(* com.example.videosharingapi.repository.VideoRepository.saveAndFlush(*))) &&
            args(videoEntity)""")
    public void saveVideoDocument(Video videoEntity) {
        var videoDoc = new com.example.videosharingapi.document.Video();
        videoDoc.setId(videoEntity.getId());
        videoDoc.setTitle(videoEntity.getTitle());
        videoDoc.setDescription(videoEntity.getDescription());
        videoDoc.setPublishedDate(videoEntity.getPublishedAt());
        videoDoc.setViewCount(videoEntity.getVideoStatistic().getViewCount());
        videoElasticsearchRepository.save(videoDoc);
    }

    @AfterReturning("""
            (execution(* com.example.videosharingapi.repository.ViewHistoryRepository.save(*)) ||
            execution(* com.example.videosharingapi.repository.ViewHistoryRepository.saveAndFlush(*))) &&
            args(viewHistory)""")
    public void updateViewCount(ViewHistory viewHistory) {
        var videoId = viewHistory.getVideo().getId();
        videoStatisticRepository.findById(videoId)
                .ifPresent(videoStat -> videoElasticsearchRepository
                        .findById(videoId)
                        .ifPresent(videoDoc -> {
                            videoDoc.setViewCount(videoStat.getViewCount());
                            videoElasticsearchRepository.save(videoDoc);
                        }));
    }

    @AfterReturning("""
            (execution(* com.example.videosharingapi.repository.VideoRepository.deleteById(*)) && args(videoId))
            """)
    public void deleteVideoDocument(String videoId) {
        videoElasticsearchRepository.deleteById(videoId);
    }

    @AfterReturning("""
            (execution(* com.example.videosharingapi.repository.UserRepository.save(*)) ||
            execution(* com.example.videosharingapi.repository.UserRepository.saveAndFlush(*))) &&
            args(userEntity)""")
    public void saveUserDocument(User userEntity) {
        var userDoc = new com.example.videosharingapi.document.User();
        userDoc.setId(userEntity.getId());
        userDoc.setUsername(userEntity.getUsername());
        userDoc.setBio(userEntity.getBio());
        userElasticsearchRepository.save(userDoc);
    }

    @AfterReturning("""
            (execution(* com.example.videosharingapi.repository.UserRepository.deleteById(*)) && args(userId))
            """)
    public void deleteUserDocument(String userId) {
        userElasticsearchRepository.deleteById(userId);
    }

    @AfterReturning("""
            (execution(* com.example.videosharingapi.repository.PlaylistRepository.save(*)) ||
            execution(* com.example.videosharingapi.repository.PlaylistRepository.saveAndFlush(*))) &&
            args(playlistEntity)""")
    public void savePlaylistDocument(Playlist playlistEntity) {
        var playlistDoc = new com.example.videosharingapi.document.Playlist();
        playlistDoc.setId(playlistEntity.getId());
        playlistDoc.setTitle(playlistEntity.getTitle());
        playlistDoc.setDescription(playlistEntity.getDescription());
        playlistDoc.setVisible(playlistEntity.getPrivacy().getStatus() == Privacy.Status.PUBLIC);
        playlistElasticsearchRepository.save(playlistDoc);
    }

    @AfterReturning("""
            (execution(* com.example.videosharingapi.repository.PlaylistRepository.deleteById(*)) && args(playlistId))
            """)
    public void deletePlaylistDocument(String playlistId) {
        playlistElasticsearchRepository.deleteById(playlistId);
    }
}
