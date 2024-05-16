package com.example.videosharingapi.repository;

import com.example.videosharingapi.entity.PlaylistItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PlaylistItemRepository extends JpaRepository<PlaylistItem, PlaylistItem.PlaylistItemId> {

    void deleteByVideoId(String videoId);

    List<PlaylistItem> findAllByPlaylistId(String playlistId);
}
