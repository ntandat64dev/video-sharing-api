package com.example.videosharingapi.repository;

import com.example.videosharingapi.model.entity.VideoLike;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VideoLikeRepository extends JpaRepository<VideoLike, VideoLike.VideoLikeId> {
}
