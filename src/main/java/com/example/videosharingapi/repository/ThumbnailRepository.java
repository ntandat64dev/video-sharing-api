package com.example.videosharingapi.repository;

import com.example.videosharingapi.entity.Thumbnail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ThumbnailRepository extends JpaRepository<Thumbnail, String> {

    @Query("SELECT t FROM Video v JOIN v.thumbnails t WHERE v.id = :videoId")
    List<Thumbnail> findAllByVideoId(String videoId);
}
