package com.example.videosharingapi.repository;

import com.example.videosharingapi.model.entity.PlaylistVideo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PlaylistVideoRepository extends JpaRepository<PlaylistVideo, PlaylistVideo.PlaylistVideoId> {
}
