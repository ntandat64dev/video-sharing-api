package com.example.videosharingapi.repository;

import com.example.videosharingapi.entity.Video;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VideoRepository extends JpaRepository<Video, String> {
    List<Video> findAllByUserId(String userId);

    Long countByUserId(String userId);
}
