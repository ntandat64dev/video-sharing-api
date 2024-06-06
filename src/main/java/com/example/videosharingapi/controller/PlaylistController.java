package com.example.videosharingapi.controller;

import com.example.videosharingapi.config.security.AuthenticatedUser;
import com.example.videosharingapi.dto.PlaylistDto;
import com.example.videosharingapi.dto.response.PageResponse;
import com.example.videosharingapi.entity.Playlist;
import com.example.videosharingapi.service.PlaylistService;
import com.example.videosharingapi.validation.IdExists;
import com.example.videosharingapi.validation.group.Create;
import com.example.videosharingapi.validation.group.Update;
import jakarta.validation.groups.Default;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/playlists")
@Validated
@RequiredArgsConstructor
public class PlaylistController {

    private final PlaylistService playlistService;

    @PostMapping
    public ResponseEntity<PlaylistDto> createPlaylist(
            @RequestBody
            @Validated({ Default.class, Create.class })
            PlaylistDto playlistDto
    ) {
        var response = playlistService.createPlaylist(playlistDto);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PutMapping
    public ResponseEntity<PlaylistDto> updatePlaylist(
            @RequestBody
            @Validated({ Default.class, Update.class })
            PlaylistDto playlistDto
    ) {
        var response = playlistService.updatePlaylist(playlistDto);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping
    public ResponseEntity<?> deletePlaylist(@IdExists(entity = Playlist.class) String id) {
        playlistService.deletePlaylist(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping("/mine")
    public ResponseEntity<PageResponse<PlaylistDto>> getMyPlaylists(
            @AuthenticationPrincipal AuthenticatedUser user,
            Pageable pageable
    ) {
        var response = playlistService.getPlaylistsByUserId(user.getUserId(), pageable);
        return ResponseEntity.ok(response);
    }
}
