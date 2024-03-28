package com.example.videosharingapi.repository;

import com.example.videosharingapi.model.entity.Playlist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface PlaylistRepository extends JpaRepository<Playlist, UUID> {
}