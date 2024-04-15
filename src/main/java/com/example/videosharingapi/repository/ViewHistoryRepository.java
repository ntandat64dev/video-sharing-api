package com.example.videosharingapi.repository;

import com.example.videosharingapi.model.entity.ViewHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ViewHistoryRepository extends JpaRepository<ViewHistory, UUID> {
    long countByVideoId(UUID videoId);

    @Query("SELECT vh FROM ViewHistory vh WHERE vh.video.id = :videoId AND vh.user.id = :userId " +
            "ORDER BY vh.publishedAt DESC LIMIT 1")
    ViewHistory findLatest(UUID videoId, UUID userId);
}
