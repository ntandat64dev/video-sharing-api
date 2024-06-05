package com.example.videosharingapi.service.impl;

import com.example.videosharingapi.dto.PlaylistDto;
import com.example.videosharingapi.dto.response.PageResponse;
import com.example.videosharingapi.entity.Playlist;
import com.example.videosharingapi.entity.Privacy;
import com.example.videosharingapi.entity.User;
import com.example.videosharingapi.exception.AppException;
import com.example.videosharingapi.exception.ErrorCode;
import com.example.videosharingapi.mapper.PlaylistMapper;
import com.example.videosharingapi.repository.PlaylistItemRepository;
import com.example.videosharingapi.repository.PlaylistRepository;
import com.example.videosharingapi.repository.PrivacyRepository;
import com.example.videosharingapi.service.PlaylistService;
import com.example.videosharingapi.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class PlaylistServiceImpl implements PlaylistService {

    private final PlaylistRepository playlistRepository;
    private final PlaylistItemRepository playlistItemRepository;
    private final PrivacyRepository privacyRepository;

    private final UserService userService;
    private final PlaylistMapper playlistMapper;

    @Override
    @Transactional
    public void createDefaultPlaylistsForUser(User user) {
        var defaultPlaylists = playlistRepository.findAllByUserIdAndDefaultTypeIsNotNull(user.getId());
        if (!defaultPlaylists.isEmpty()) return;

        var privatePrivacy = privacyRepository.findByStatus(Privacy.Status.PRIVATE);

        var watchLaterPlaylist = Playlist.builder()
                .user(user)
                .privacy(privatePrivacy)
                .title("")
                .defaultType((byte) 0)
                .publishedAt(LocalDateTime.now())
                .build();

        var likeVideosPlaylist = Playlist.builder()
                .user(user)
                .privacy(privatePrivacy)
                .title("")
                .defaultType((byte) 1)
                .publishedAt(LocalDateTime.now())
                .build();

        playlistRepository.saveAll(List.of(watchLaterPlaylist, likeVideosPlaylist));
    }

    @Override
    @Transactional
    public PlaylistDto createPlaylist(PlaylistDto playlistDto) {
        if (!userService.getAuthenticatedUser().getUserId().equals(playlistDto.getSnippet().getUserId())) {
            // If snippet's userId is not authenticated user ID.
            throw new AppException(ErrorCode.FORBIDDEN);
        }

        var playlist = playlistMapper.toPlaylist(playlistDto);
        playlistRepository.save(playlist);

        return playlistMapper.toPlaylistDto(playlist);
    }

    @Override
    @Transactional
    public PlaylistDto updatePlaylist(PlaylistDto playlistDto) {
        var authenticatedUser = userService.getAuthenticatedUser();

        if (!authenticatedUser.getUserId().equals(playlistDto.getSnippet().getUserId())) {
            // If snippet's userId is not authenticated user ID.
            throw new AppException(ErrorCode.FORBIDDEN);
        }

        var playlist = playlistRepository.findById(playlistDto.getId()).orElseThrow();

        if (!authenticatedUser.getUserId().equals(playlist.getUser().getId())) {
            // If the authenticated user is not the one who created the playlist.
            throw new AppException(ErrorCode.FORBIDDEN);
        }

        if (playlist.getDefaultType() != null) {
            // User cannot update default playlists.
            throw new AppException(ErrorCode.UPDATE_DEFAULT_PLAYLISTS);
        }

        playlistMapper.updatePlaylist(playlist, playlistDto);

        // Trigger Playlist document update
        playlistRepository.save(playlist);

        return playlistMapper.toPlaylistDto(playlist);
    }

    @Override
    @Transactional
    public void deletePlaylist(String id) {
        var authenticatedUser = userService.getAuthenticatedUser();
        var playlist = playlistRepository.findById(id).orElseThrow();

        if (!authenticatedUser.getUserId().equals(playlist.getUser().getId())) {
            // If the authenticated user is not the one who created the playlist.
            throw new AppException(ErrorCode.FORBIDDEN);
        }

        if (playlist.getDefaultType() != null) {
            // User cannot delete default playlists.
            throw new AppException(ErrorCode.DELETE_DEFAULT_PLAYLISTS);
        }

        // Delete playlist items
        playlistItemRepository.deleteAllByPlaylistId(id);

        // Delete playlist
        playlistRepository.deleteById(id);
    }

    @Override
    public PageResponse<PlaylistDto> getPlaylistsByUserId(String userId, Pageable pageable) {
        var playlistDtoPage = playlistRepository
                .findAllByUserId(userId, pageable)
                .map(playlistMapper::toPlaylistDto);
        return new PageResponse<>(playlistDtoPage);
    }
}
