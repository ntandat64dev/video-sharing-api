package com.example.videosharingapi.repository;

import com.example.videosharingapi.entity.PlaylistItem;
import com.example.videosharingapi.entity.Video;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PlaylistItemRepository extends JpaRepository<PlaylistItem, PlaylistItem.PlaylistItemId> {

    void deleteAllByVideoId(String videoId);

    void deleteAllByPlaylistId(String playlistId);

    List<PlaylistItem> findAllByPlaylistId(String playlistId);

    Page<PlaylistItem> findAllByPlaylistId(String playlistId, Pageable pageable);

    PlaylistItem findTopByPlaylistIdOrderByPriorityAsc(String playlistId);

    Long countAllByPlaylistId(String playlistId);

    PlaylistItem findByPlaylistIdAndPlaylistUserIdAndVideoId(String playlistId, String playlistUserId, String videoId);

    @Query("SELECT COALESCE(MAX(pi.priority), 0 - 1) + 1 FROM PlaylistItem pi WHERE pi.playlist.id = :playlistId")
    Long getMaxPriorityByPlaylistId(String playlistId);

    PlaylistItem findByPlaylistIdAndVideoId(String playlistId, String videoId);

    @Query("SELECT pi.video FROM PlaylistItem pi WHERE pi.playlist.id = :playlistId")
    Page<Video> getPlaylistItemVideos(String playlistId, Pageable pageable);
}
