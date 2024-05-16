package com.example.videosharingapi.repository;

import com.example.videosharingapi.entity.VideoRating;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VideoRatingRepository extends JpaRepository<VideoRating, VideoRating.VideoRatingId> {

    void deleteByVideoId(String videoId);

    Boolean existsByUserIdAndVideoId(String userId, String videoId);

    VideoRating findByUserIdAndVideoId(String userId, String videoId);

    List<VideoRating> findByVideoId(String videoId);
}