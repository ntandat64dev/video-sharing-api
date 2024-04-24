package com.example.videosharingapi.repository;

import com.example.videosharingapi.model.entity.Video;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface VideoRepository extends JpaRepository<Video, UUID> {
    List<Video> findAllByUserId(UUID userId);

    long countByUserId(UUID userId);
}
