package com.example.videosharingapi.repository;

import com.example.videosharingapi.model.entity.VideoSpec;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface VideoSpecRepository extends JpaRepository<VideoSpec, UUID> {
}
