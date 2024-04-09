package com.example.videosharingapi.repository;

import com.example.videosharingapi.model.entity.VideoRating;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface VideoRatingRepository extends JpaRepository<VideoRating, VideoRating.VideoRatingId> {
    Boolean existsByUserIdAndVideoId(UUID userId, UUID videoId);

    VideoRating findByUserIdAndVideoId(UUID userId, UUID videoId);
}