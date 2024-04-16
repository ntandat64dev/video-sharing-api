package com.example.videosharingapi.repository;

import com.example.videosharingapi.model.entity.Thumbnail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ThumbnailRepository extends JpaRepository<Thumbnail, UUID> {

    @Query("SELECT t FROM Video v JOIN v.thumbnails t WHERE v.id = :videoId")
    List<Thumbnail> findAllByVideoId(UUID videoId);
}
