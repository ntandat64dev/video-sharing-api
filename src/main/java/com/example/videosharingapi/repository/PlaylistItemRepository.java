package com.example.videosharingapi.repository;

import com.example.videosharingapi.entity.PlaylistItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PlaylistItemRepository extends JpaRepository<PlaylistItem, PlaylistItem.PlaylistItemId> {

    void deleteAllByVideoId(String videoId);

    void deleteAllByPlaylistId(String playlistId);

    List<PlaylistItem> findAllByPlaylistId(String playlistId);

    PlaylistItem findTopByPlaylistIdOrderByPriorityAsc(String playlistId);

    Long countAllByPlaylistId(String playlistId);

    PlaylistItem findByPlaylistIdAndPlaylistUserIdAndVideoId(String playlistId, String playlistUserId, String videoId);

    @Query("SELECT IFNULL(MAX(pi.priority), 0) FROM PlaylistItem pi WHERE pi.playlist.id = :playlistId")
    Long getMaxPriorityByPlaylistId(String playlistId);
}
