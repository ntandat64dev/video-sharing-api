package com.example.videosharingapi.service.impl;

import com.example.videosharingapi.dto.PlaylistDto;
import com.example.videosharingapi.dto.PlaylistItemDto;
import com.example.videosharingapi.dto.VideoDto;
import com.example.videosharingapi.dto.VideoRatingDto;
import com.example.videosharingapi.dto.response.PageResponse;
import com.example.videosharingapi.entity.*;
import com.example.videosharingapi.exception.AppException;
import com.example.videosharingapi.exception.ErrorCode;
import com.example.videosharingapi.mapper.PlaylistItemMapper;
import com.example.videosharingapi.mapper.PlaylistMapper;
import com.example.videosharingapi.mapper.VideoMapper;
import com.example.videosharingapi.repository.PlaylistItemRepository;
import com.example.videosharingapi.repository.PlaylistRepository;
import com.example.videosharingapi.repository.PrivacyRepository;
import com.example.videosharingapi.repository.VideoRepository;
import com.example.videosharingapi.service.PlaylistService;
import com.example.videosharingapi.service.UserService;
import com.example.videosharingapi.service.VideoService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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
    private final VideoRepository videoRepository;
    private final PrivacyRepository privacyRepository;

    private final VideoService videoService;
    private final UserService userService;

    private final PlaylistMapper playlistMapper;
    private final PlaylistItemMapper playlistItemMapper;
    private final VideoMapper videoMapper;

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
    public PlaylistDto getPlaylistById(String id) {
        var playlist = playlistRepository.findById(id).orElseThrow();
        if (isPlaylistNotAccessible(playlist)) throw new AppException(ErrorCode.ACCESS_FORBIDDEN_PLAYLIST);

        return playlistMapper.toPlaylistDto(playlist);
    }

    @Override
    public PageResponse<PlaylistDto> getPlaylistsByUserId(String userId, Pageable pageable) {
        var playlistDtoPage = playlistRepository
                .findAllByUserId(userId, pageable)
                .map(playlistMapper::toPlaylistDto);
        return new PageResponse<>(playlistDtoPage);
    }

    @Override
    public List<String> getPlaylistIdsContainingVideo(String videoId, List<Integer> excludes) {
        var user = userService.getAuthenticatedUser();
        return playlistRepository.findPlaylistIdsContainingVideo(videoId, user.getUserId(), excludes);
    }

    @Override
    @Transactional
    public PlaylistItem createPlaylistItem(Playlist playlist, Video video) {
        var maxPriority = playlistItemRepository.getMaxPriorityByPlaylistId(playlist.getId());
        var playlistItem = new PlaylistItem();
        playlistItem.setPlaylist(playlist);
        playlistItem.setVideo(video);
        playlistItem.setPriority(maxPriority);
        playlistItemRepository.save(playlistItem);
        return playlistItem;
    }

    @Override
    @Transactional
    public PlaylistItemDto createPlaylistItem(PlaylistItemDto playlistItemDto) {
        var snippet = playlistItemDto.getSnippet();
        var playlist = playlistRepository.findById(snippet.getPlaylistId()).orElseThrow();

        if (!userService.getAuthenticatedUser().getUserId().equals(playlist.getUser().getId())) {
            // If the user is not the owner of the playlist.
            throw new AppException(ErrorCode.FORBIDDEN);
        }

        if (playlist.getDefaultType() != null && playlist.getDefaultType() == 1) {
            // If the playlist is Liked Videos, then throw error
            // Liked Videos playlist can only be updated when the user liked video.
            throw new AppException(ErrorCode.PLAYLIST_ITEM_ADD_TO_LIKED_VIDEOS);
        }

        if (playlistItemRepository.findByPlaylistIdAndVideoId(snippet.getPlaylistId(), snippet.getVideoId()) != null) {
            // If playlist item already exists in the playlist.
            throw new AppException(ErrorCode.PLAYLIST_ITEM_ALREADY_EXISTS);
        }

        var video = videoRepository.findById(snippet.getVideoId()).orElseThrow();
        var playlistItem = createPlaylistItem(playlist, video);
        return playlistItemMapper.toPlaylistItemDto(playlistItem);
    }

    @Override
    @Transactional
    public void deletePlaylistItem(String playlistId, String videoId) {
        var user = userService.getAuthenticatedUser();

        var playlistItem = playlistItemRepository.findByPlaylistIdAndVideoId(playlistId, videoId);
        if (playlistItem == null) throw new AppException(ErrorCode.PLAYLIST_ITEM_NOT_FOUNT);

        if (!playlistItem.getPlaylist().getUser().getId().equals(user.getUserId())) {
            // If the user is not the owner of the playlist.
            throw new AppException(ErrorCode.FORBIDDEN);
        }

        playlistItemRepository.delete(playlistItem);

        if (playlistItem.getPlaylist().getDefaultType() != null && playlistItem.getPlaylist().getDefaultType() == 1) {
            videoService.rateVideo(playlistItem.getVideo().getId(), user.getUserId(), VideoRatingDto.NONE);
        }
    }

    @Override
    public PageResponse<PlaylistItemDto> getPlaylistItemsByPlaylistId(String playlistId, Pageable pageable) {
        var playlist = playlistRepository.findById(playlistId).orElseThrow();
        if (isPlaylistNotAccessible(playlist)) throw new AppException(ErrorCode.ACCESS_FORBIDDEN_PLAYLIST);

        var sort = Sort.by("priority");
        var newPage = pageable.isPaged()
                ? PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), sort)
                : Pageable.unpaged(sort);

        var playlistItemDtoPage = playlistItemRepository
                .findAllByPlaylistId(playlistId, newPage)
                .map(playlistItemMapper::toPlaylistItemDto);
        return new PageResponse<>(playlistItemDtoPage);
    }

    private boolean isPlaylistNotAccessible(Playlist playlist) {
        var user = userService.getAuthenticatedUser();
        return !playlist.getUser().getId().equals(user.getUserId()) &&
                (playlist.getDefaultType() != null || playlist.getPrivacy().getStatus() == Privacy.Status.PRIVATE);
    }

    @Override
    public PageResponse<VideoDto> getPlaylistItemVideos(String playlistId, Pageable pageable) {
        var playlist = playlistRepository.findById(playlistId).orElseThrow();
        if (isPlaylistNotAccessible(playlist)) throw new AppException(ErrorCode.ACCESS_FORBIDDEN_PLAYLIST);

        var sort = Sort.by("priority");
        var newPage = pageable.isPaged()
                ? PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), sort)
                : Pageable.unpaged(sort);

        var videoDtoPage = playlistItemRepository
                .getPlaylistItemVideos(playlistId, newPage)
                .map(videoMapper::toVideoDto);
        return new PageResponse<>(videoDtoPage);
    }
}