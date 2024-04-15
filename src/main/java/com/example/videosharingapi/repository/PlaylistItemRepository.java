package com.example.videosharingapi.repository;

import com.example.videosharingapi.model.entity.PlaylistItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PlaylistItemRepository extends JpaRepository<PlaylistItem, PlaylistItem.PlaylistItemId> {
}
