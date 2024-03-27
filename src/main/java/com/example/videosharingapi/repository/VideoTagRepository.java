package com.example.videosharingapi.repository;

import com.example.videosharingapi.model.entity.VideoTag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VideoTagRepository extends JpaRepository<VideoTag, VideoTag.VideoTagId> {
}
