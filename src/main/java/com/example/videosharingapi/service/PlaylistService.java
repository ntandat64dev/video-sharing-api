package com.example.videosharingapi.service;

import com.example.videosharingapi.dto.PlaylistDto;
import com.example.videosharingapi.dto.PlaylistItemDto;
import com.example.videosharingapi.dto.VideoDto;
import com.example.videosharingapi.dto.response.PageResponse;
import com.example.videosharingapi.entity.Playlist;
import com.example.videosharingapi.entity.PlaylistItem;
import com.example.videosharingapi.entity.User;
import com.example.videosharingapi.entity.Video;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface PlaylistService {

    void createDefaultPlaylistsForUser(User user);

    PlaylistDto createPlaylist(PlaylistDto playlistDto);

    PlaylistDto updatePlaylist(PlaylistDto playlistDto);

    void deletePlaylist(String id);

    PlaylistDto getPlaylistById(String id);

    PageResponse<PlaylistDto> getPlaylistsByUserId(String userId, Pageable pageable);

    List<String> getPlaylistIdsContainingVideo(String videoId, List<Integer> excludes);

    PlaylistItem createPlaylistItem(Playlist playlist, Video video);

    PlaylistItemDto createPlaylistItem(PlaylistItemDto playlistItemDto);

    void deletePlaylistItem(String playlistId, String videoId);

    PageResponse<PlaylistItemDto> getPlaylistItemsByPlaylistId(String playlistId, Pageable pageable);

    PageResponse<VideoDto> getPlaylistItemVideos(String playlistId, Pageable pageable);
}