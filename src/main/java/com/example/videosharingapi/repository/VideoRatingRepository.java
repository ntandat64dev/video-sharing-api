package com.example.videosharingapi.repository;

import com.example.videosharingapi.entity.VideoRating;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VideoRatingRepository extends JpaRepository<VideoRating, VideoRating.VideoRatingId> {
    Boolean existsByUserIdAndVideoId(String userId, String videoId);

    VideoRating findByUserIdAndVideoId(String userId, String videoId);
}