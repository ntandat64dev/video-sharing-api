package com.example.videosharingapi.service;

import com.example.videosharingapi.dto.PlaylistDto;
import com.example.videosharingapi.dto.response.PageResponse;
import com.example.videosharingapi.entity.User;
import org.springframework.data.domain.Pageable;

public interface PlaylistService {

    void createDefaultPlaylistsForUser(User user);

    PlaylistDto createPlaylist(PlaylistDto playlistDto);

    PlaylistDto updatePlaylist(PlaylistDto playlistDto);

    void deletePlaylist(String id);

    PageResponse<PlaylistDto> getPlaylistsByUserId(String userId, Pageable pageable);
}