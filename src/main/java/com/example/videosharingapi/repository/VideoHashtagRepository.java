package com.example.videosharingapi.repository;

import com.example.videosharingapi.model.entity.VideoHashtag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface VideoHashtagRepository extends JpaRepository<VideoHashtag, VideoHashtag.VideoTagId> {
    List<VideoHashtag> findByVideoId(UUID videoId);
}
