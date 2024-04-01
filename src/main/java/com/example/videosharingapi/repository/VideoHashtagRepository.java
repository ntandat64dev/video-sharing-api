package com.example.videosharingapi.repository;

import com.example.videosharingapi.model.entity.VideoHashtag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VideoHashtagRepository extends JpaRepository<VideoHashtag, VideoHashtag.VideoTagId> {
}
