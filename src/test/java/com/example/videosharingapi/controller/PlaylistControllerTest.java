package com.example.videosharingapi.controller;

import com.example.videosharingapi.common.AbstractElasticsearchContainer;
import com.example.videosharingapi.common.TestSql;
import com.example.videosharingapi.common.TestUtil;
import com.example.videosharingapi.dto.PlaylistDto;
import com.example.videosharingapi.dto.PlaylistItemDto;
import com.example.videosharingapi.dto.VideoRatingDto;
import com.example.videosharingapi.elasticsearchrepository.PlaylistElasticsearchRepository;
import com.example.videosharingapi.entity.Playlist;
import com.example.videosharingapi.entity.Privacy;
import com.example.videosharingapi.repository.PlaylistItemRepository;
import com.example.videosharingapi.repository.PlaylistRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.assertj.core.util.Streams;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
@TestSql
@WithUserDetails("user1")
public class PlaylistControllerTest extends AbstractElasticsearchContainer {

    private @Autowired PlaylistRepository playlistRepository;
    private @Autowired PlaylistItemRepository playlistItemRepository;
    private @Autowired PlaylistElasticsearchRepository playlistElasticsearchRepository;

    private @Autowired MockMvc mockMvc;
    private @Autowired ObjectMapper objectMapper;
    private @Autowired TestUtil testUtil;

    private PlaylistDto obtainPlaylistDto() {
        var playlistDto = new PlaylistDto();
        playlistDto.setSnippet(PlaylistDto.Snippet.builder()
                .title("Playlist test")
                .userId("a05990b1")
                .build());
        playlistDto.setStatus(PlaylistDto.Status.builder()
                .privacy("public")
                .build());
        return playlistDto;
    }

    private PlaylistDto obtainPlaylistDtoForUpdate() {
        var playlistDto = new PlaylistDto();
        playlistDto.setId("d8659362");
        playlistDto.setSnippet(PlaylistDto.Snippet.builder()
                .title("My Videos (updated)")
                .description("My Videos playlist description")
                .userId("a05990b1")
                .build());
        playlistDto.setStatus(PlaylistDto.Status.builder()
                .privacy("private")
                .build());
        return playlistDto;
    }

    private PlaylistItemDto obtainPlaylistItemDto() {
        var playlistItemDto = new PlaylistItemDto();
        playlistItemDto.setSnippet(PlaylistItemDto.Snippet.builder()
                .playlistId("fae06c8a")
                .videoId("e65707b4")
                .build());
        return playlistItemDto;
    }

    @Test
    @Transactional
    public void givenPlaylistDto_whenCreate_thenSuccess() throws Exception {
        var playlistDto = obtainPlaylistDto();
        mockMvc.perform(post("/api/v1/playlists")
                        .content(objectMapper.writeValueAsString(playlistDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());
    }

    @Test
    @Transactional
    public void givenPlaylistDto_whenCreate_thenDatabaseIsUpdated() throws Exception {
        var playlistDto = obtainPlaylistDto();
        var result = mockMvc.perform(post("/api/v1/playlists")
                        .content(objectMapper.writeValueAsString(playlistDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andReturn();

        assertThat(playlistRepository.count()).isEqualTo(6);
        var playlist = playlistRepository.findById(testUtil.json(result, "$.id")).orElseThrow();
        assertThat(playlist.getTitle()).isEqualTo("Playlist test");
        assertThat(playlist.getDescription()).isEqualTo(null);
        assertThat(playlist.getDefaultType()).isEqualTo(null);
        assertThat(playlist.getPublishedAt()).isBetween(LocalDateTime.now().minusMinutes(10), LocalDateTime.now());
        assertThat(playlist.getUser().getId()).isEqualTo("a05990b1");
        assertThat(playlist.getPrivacy().getStatus()).isEqualTo(Privacy.Status.PUBLIC);
    }

    @Test
    @Transactional
    public void givenPlaylistDto_whenCreate_thenPlaylistDocumentIsCreated() throws Exception {
        super.prepareData();
        var playlistDto = obtainPlaylistDto();
        var result = mockMvc.perform(post("/api/v1/playlists")
                        .content(objectMapper.writeValueAsString(playlistDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andReturn();

        var playlistId = (String) testUtil.json(result, "$.id");

        assertThat(playlistElasticsearchRepository.count()).isEqualTo(6);
        var playlistDoc = playlistElasticsearchRepository.findById(playlistId).orElseThrow();
        assertThat(playlistDoc.getId()).isEqualTo(playlistId);
        assertThat(playlistDoc.getTitle()).isEqualTo("Playlist test");
        assertThat(playlistDoc.getDescription()).isEqualTo(null);
    }

    @Test
    public void givenInvalidPlaylistDto_whenCreate_thenFailure() throws Exception {
        // Given not null id
        var playlistDto = obtainPlaylistDto();
        playlistDto.setId("12345678");
        mockMvc.perform(post("/api/v1/playlists")
                        .content(objectMapper.writeValueAsString(playlistDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors[0]")
                        .value("id: must be null"));

        // Given null userId
        playlistDto = obtainPlaylistDto();
        playlistDto.getSnippet().setUserId(null);
        mockMvc.perform(post("/api/v1/playlists")
                        .content(objectMapper.writeValueAsString(playlistDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors[0]")
                        .value("snippet.userId: must not be null"));

        // Given non-existent userId
        playlistDto = obtainPlaylistDto();
        playlistDto.getSnippet().setUserId("12345678");
        mockMvc.perform(post("/api/v1/playlists")
                        .content(objectMapper.writeValueAsString(playlistDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors[0]")
                        .value("snippet.userId: ID does not exist."));

        // Given exist but invalid userId
        playlistDto = obtainPlaylistDto();
        playlistDto.getSnippet().setUserId("9b79f4ba");
        mockMvc.perform(post("/api/v1/playlists")
                        .content(objectMapper.writeValueAsString(playlistDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());

        // Given null title
        playlistDto = obtainPlaylistDto();
        playlistDto.getSnippet().setTitle(null);
        mockMvc.perform(post("/api/v1/playlists")
                        .content(objectMapper.writeValueAsString(playlistDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors[0]")
                        .value("snippet.title: must not be blank"));

        // Given blank title
        playlistDto = obtainPlaylistDto();
        playlistDto.getSnippet().setTitle("");
        mockMvc.perform(post("/api/v1/playlists")
                        .content(objectMapper.writeValueAsString(playlistDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors[0]")
                        .value("snippet.title: must not be blank"));

        // Given invalid privacy
        playlistDto = obtainPlaylistDto();
        playlistDto.getStatus().setPrivacy("privates");
        mockMvc.perform(post("/api/v1/playlists")
                        .content(objectMapper.writeValueAsString(playlistDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors[0]")
                        .value("status.privacy: must match \"(?i)(private|public)\""));
    }

    @Test
    @Transactional
    public void givenPlaylistDto_whenUpdate_thenSuccess() throws Exception {
        var playlistDto = obtainPlaylistDtoForUpdate();
        mockMvc.perform(put("/api/v1/playlists")
                        .content(objectMapper.writeValueAsString(playlistDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    @Transactional
    public void givenPlaylistDto_whenUpdate_thenDatabaseIsUpdate() throws Exception {
        var playlistDto = obtainPlaylistDtoForUpdate();
        mockMvc.perform(put("/api/v1/playlists")
                        .content(objectMapper.writeValueAsString(playlistDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        assertThat(playlistRepository.count()).isEqualTo(5);
        var playlist = playlistRepository.findById("d8659362").orElseThrow();
        assertThat(playlist.getTitle()).isEqualTo("My Videos (updated)");
        assertThat(playlist.getDescription()).isEqualTo("My Videos playlist description");
        assertThat(playlist.getDefaultType()).isEqualTo(null);
        assertThat(playlist.getPrivacy().getStatus()).isEqualTo(Privacy.Status.PRIVATE);
        assertThat(playlist.getUser().getId()).isEqualTo("a05990b1");
    }

    @Test
    @Transactional
    public void givenPlaylistDto_whenUpdate_thenPlaylistDocumentIsUpdate() throws Exception {
        super.prepareData();
        var playlistDto = obtainPlaylistDtoForUpdate();
        mockMvc.perform(put("/api/v1/playlists")
                        .content(objectMapper.writeValueAsString(playlistDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        assertThat(playlistElasticsearchRepository.count()).isEqualTo(5);
        var playlistDoc = playlistElasticsearchRepository.findById("d8659362").orElseThrow();
        assertThat(playlistDoc.getId()).isEqualTo("d8659362");
        assertThat(playlistDoc.getTitle()).isEqualTo("My Videos (updated)");
        assertThat(playlistDoc.getDescription()).isEqualTo("My Videos playlist description");
    }

    @Test
    public void givenInvalidPlaylistDto_whenUpdate_thenFailure() throws Exception {
        // Given null id
        var playlistDto = obtainPlaylistDtoForUpdate();
        playlistDto.setId(null);
        mockMvc.perform(put("/api/v1/playlists")
                        .content(objectMapper.writeValueAsString(playlistDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors[0]")
                        .value("id: must not be null"));

        // Given non-existent id
        playlistDto = obtainPlaylistDtoForUpdate();
        playlistDto.setId("12345678");
        mockMvc.perform(put("/api/v1/playlists")
                        .content(objectMapper.writeValueAsString(playlistDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors[0]")
                        .value("id: ID does not exist."));

        // Given user-did-not-create playlist's id
        playlistDto = obtainPlaylistDtoForUpdate();
        playlistDto.setId("236e2aa6");
        mockMvc.perform(put("/api/v1/playlists")
                        .content(objectMapper.writeValueAsString(playlistDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.message")
                        .value("The request is not supported."));

        // Given default playlist's id
        playlistDto = obtainPlaylistDtoForUpdate();
        playlistDto.setId("fae06c8a");
        mockMvc.perform(put("/api/v1/playlists")
                        .content(objectMapper.writeValueAsString(playlistDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.message")
                        .value("You cannot update the default playlists."));

        // Given invalid userId
        playlistDto = obtainPlaylistDtoForUpdate();
        playlistDto.getSnippet().setUserId("9b79f4ba");
        mockMvc.perform(put("/api/v1/playlists")
                        .content(objectMapper.writeValueAsString(playlistDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.message")
                        .value("The request is not supported."));
    }

    @Test
    @Transactional
    public void givenPlaylistId_whenDelete_thenSuccess() throws Exception {
        var playlistId = "d8659362";
        mockMvc.perform(delete("/api/v1/playlists")
                        .param("id", playlistId))
                .andExpect(status().isNoContent());
    }

    @Test
    @Transactional
    public void givenPlaylistId_whenDelete_thenDatabaseIsUpdated() throws Exception {
        var playlistId = "d8659362";
        mockMvc.perform(delete("/api/v1/playlists")
                        .param("id", playlistId))
                .andExpect(status().isNoContent());

        assertThat(playlistRepository.count()).isEqualTo(4);
        assertThat(playlistRepository.findById("d8659362")).isNotPresent();
        assertThat(playlistRepository.findAll().stream().map(Playlist::getId))
                .containsExactlyInAnyOrder("fae06c8a", "c31760ea", "236e2aa6", "d07f1bee");

        assertThat(playlistItemRepository.count()).isEqualTo(1);
        assertThat(playlistItemRepository.findAllByPlaylistId("d8659362")).isEmpty();
    }

    @Test
    @Transactional
    public void givenPlaylistId_whenDelete_thenPlaylistDocumentIsDeleted() throws Exception {
        super.prepareData();
        var playlistId = "d8659362";
        mockMvc.perform(delete("/api/v1/playlists")
                        .param("id", playlistId))
                .andExpect(status().isNoContent());

        assertThat(playlistElasticsearchRepository.count()).isEqualTo(4);
        assertThat(playlistElasticsearchRepository.findById("d8659362")).isNotPresent();
        assertThat(Streams.stream(playlistElasticsearchRepository.findAll())
                .map(com.example.videosharingapi.document.Playlist::getId))
                .containsExactlyInAnyOrder("fae06c8a", "c31760ea", "236e2aa6", "d07f1bee");
    }

    @Test
    public void givenInvalidPlaylistId_whenDelete_thenFailure() throws Exception {
        // Given non-existent id
        var playlistId = "12345678";
        mockMvc.perform(delete("/api/v1/playlists")
                        .param("id", playlistId))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors[0]")
                        .value("id: ID does not exist."));

        // Given default playlist id
        playlistId = "fae06c8a";
        mockMvc.perform(delete("/api/v1/playlists")
                        .param("id", playlistId))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.message")
                        .value("You cannot delete the default playlists."));
    }

    @Test
    public void givenPlaylistId_whenGetPlaylist_thenSuccess() throws Exception {
        mockMvc.perform(get("/api/v1/playlists")
                        .param("id", "c31760ea"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.snippet.title")
                        .value("Liked Videos"))
                .andExpect(jsonPath("$.snippet.description")
                        .value(nullValue()))
                .andExpect(jsonPath("$.snippet.userId")
                        .value("a05990b1"))
                .andExpect(jsonPath("$.snippet.userImageUrl")
                        .value("User 1 default thumbnail URL"))
                .andExpect(jsonPath("$.snippet.thumbnails.length()")
                        .value(0))
                .andExpect(jsonPath("$.status.isDefaultPlaylist")
                        .value(true))
                .andExpect(jsonPath("$.status.privacy")
                        .value("PRIVATE"))
                .andExpect(jsonPath("$.contentDetails.itemCount")
                        .value(0));
    }

    @Test
    public void whenGetDefaultPlaylistOfOtherUser_thenForbidden() throws Exception {
        mockMvc.perform(get("/api/v1/playlists")
                        .param("id", "236e2aa6"))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.message")
                        .value("You cannot access this playlist."));
    }

    @Test
    public void givenVideoId_whenGetPlaylistIdsContainingThatVideo_thenSuccess() throws Exception {
        var videoId = "e65707b4";
        mockMvc.perform(get("/api/v1/playlists/containing")
                        .param("videoId", videoId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[*]")
                        .value(containsInAnyOrder("d8659362")));

        videoId = "37b32dc2";
        mockMvc.perform(get("/api/v1/playlists/containing")
                        .param("videoId", videoId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));

        videoId = "e65707b4";
        mockMvc.perform(get("/api/v1/playlists/containing")
                        .param("videoId", videoId)
                        .param("excludes", "0"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[*]")
                        .value(containsInAnyOrder("d8659362")));
    }

    @Test
    public void whenGetMyPlaylists_thenSuccess() throws Exception {
        mockMvc.perform(get("/api/v1/playlists/mine"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalElements")
                        .value(3))
                .andExpect(jsonPath("$.items[*].id")
                        .value(containsInAnyOrder("fae06c8a", "c31760ea", "d8659362")))

                .andExpect(jsonPath("$.items[?(@.id == 'fae06c8a')].snippet.title")
                        .value("Watch Later"))
                .andExpect(jsonPath("$.items[?(@.id == 'fae06c8a')].snippet.description")
                        .value(contains(nullValue())))
                .andExpect(jsonPath("$.items[?(@.id == 'fae06c8a')].snippet.userId")
                        .value("a05990b1"))
                .andExpect(jsonPath("$.items[?(@.id == 'fae06c8a')].snippet.userImageUrl")
                        .value("User 1 default thumbnail URL"))
                .andExpect(jsonPath("$.items[?(@.id == 'fae06c8a')].snippet.thumbnails.length()")
                        .value(0))
                .andExpect(jsonPath("$.items[?(@.id == 'fae06c8a')].status.isDefaultPlaylist")
                        .value(true))
                .andExpect(jsonPath("$.items[?(@.id == 'fae06c8a')].status.privacy")
                        .value("PRIVATE"))
                .andExpect(jsonPath("$.items[?(@.id == 'fae06c8a')].contentDetails.itemCount")
                        .value(0))

                .andExpect(jsonPath("$.items[?(@.id == 'd8659362')].snippet.title")
                        .value("My Videos"))
                .andExpect(jsonPath("$.items[?(@.id == 'd8659362')].snippet.description")
                        .value(contains(nullValue())))
                .andExpect(jsonPath("$.items[?(@.id == 'd8659362')].snippet.userId")
                        .value("a05990b1"))
                .andExpect(jsonPath("$.items[?(@.id == 'fae06c8a')].snippet.userImageUrl")
                        .value("User 1 default thumbnail URL"))
                .andExpect(jsonPath("$.items[?(@.id == 'd8659362')].snippet.thumbnails.length()")
                        .value(1))
                .andExpect(jsonPath("$.items[?(@.id == 'd8659362')].snippet.thumbnails['DEFAULT'].url")
                        .value("Video 2 default thumbnail URL"))
                .andExpect(jsonPath("$.items[?(@.id == 'd8659362')].status.isDefaultPlaylist")
                        .value(false))
                .andExpect(jsonPath("$.items[?(@.id == 'd8659362')].status.privacy")
                        .value("PUBLIC"))
                .andExpect(jsonPath("$.items[?(@.id == 'd8659362')].contentDetails.itemCount")
                        .value(2));
    }

    @Test
    public void whenGetMyPlaylists_thenReturnInExpectOrder() throws Exception {
        mockMvc.perform(get("/api/v1/playlists/mine"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalElements")
                        .value(3))
                .andExpect(jsonPath("$.items[*].id")
                        .value(contains("fae06c8a", "c31760ea", "d8659362")));
    }

    @Test
    @Transactional
    public void givenPlaylistItemDto_whenCreatePlaylistItem_thenSuccess() throws Exception {
        var playlistItemDto = obtainPlaylistItemDto();
        mockMvc.perform(post("/api/v1/playlists/items")
                        .content(objectMapper.writeValueAsString(playlistItemDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.snippet.playlistId").value("fae06c8a"))
                .andExpect(jsonPath("$.snippet.videoId").value("e65707b4"))
                .andExpect(jsonPath("$.snippet.title").value("Video 3"))
                .andExpect(jsonPath("$.snippet.description").value("Video 3 description"))
                .andExpect(jsonPath("$.snippet.videoUrl").value("Video 3 URL"))
                .andExpect(jsonPath("$.snippet.thumbnails.length()").value(1))
                .andExpect(jsonPath("$.snippet.thumbnails['DEFAULT'].url")
                        .value("Video 3 default thumbnail URL"))
                .andExpect(jsonPath("$.snippet.priority").value(0))
                .andExpect(jsonPath("$.snippet.videoOwnerUserId").value("9b79f4ba"))
                .andExpect(jsonPath("$.snippet.videoOwnerUsername").value("user2"))
                .andExpect(jsonPath("$.contentDetails.duration").value("PT50M"));
    }

    @Test
    @Transactional
    public void givenPlaylistItemDto_whenCreatePlaylistItem_thenDatabaseIsUpdated() throws Exception {
        var playlistItemDto = obtainPlaylistItemDto();
        mockMvc.perform(post("/api/v1/playlists/items")
                        .content(objectMapper.writeValueAsString(playlistItemDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());

        assertThat(playlistItemRepository.count()).isEqualTo(4);
        var playlistItem = playlistItemRepository.findByPlaylistIdAndVideoId("fae06c8a", "e65707b4");
        assertThat(playlistItem.getPriority()).isEqualTo(0);

        // Create another item
        playlistItemDto.getSnippet().setVideoId("f7d9b74b");
        mockMvc.perform(post("/api/v1/playlists/items")
                        .content(objectMapper.writeValueAsString(playlistItemDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());

        assertThat(playlistItemRepository.count()).isEqualTo(5);
        playlistItem = playlistItemRepository.findByPlaylistIdAndVideoId("fae06c8a", "f7d9b74b");
        assertThat(playlistItem.getPriority()).isEqualTo(1);
    }

    @Test
    @Transactional
    public void whenCreatePlaylistItem_thenGetPlaylist_thenItemCountAndThumbnailsReturnAsExpect() throws Exception {
        mockMvc.perform(post("/api/v1/playlists/items")
                        .content(objectMapper.writeValueAsString(obtainPlaylistItemDto()))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/api/v1/playlists")
                        .param("id", "fae06c8a"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.snippet.title")
                        .value("Watch Later"))
                .andExpect(jsonPath("$.snippet.description")
                        .value(nullValue()))
                .andExpect(jsonPath("$.snippet.userId")
                        .value("a05990b1"))
                .andExpect(jsonPath("$.snippet.userImageUrl")
                        .value("User 1 default thumbnail URL"))
                .andExpect(jsonPath("$.snippet.thumbnails.length()")
                        .value(1))
                .andExpect(jsonPath("$.snippet.thumbnails['DEFAULT'].url")
                        .value("Video 3 default thumbnail URL"))
                .andExpect(jsonPath("$.status.isDefaultPlaylist")
                        .value(true))
                .andExpect(jsonPath("$.status.privacy")
                        .value("PRIVATE"))
                .andExpect(jsonPath("$.contentDetails.itemCount")
                        .value(1));

        // Add another item
        var playlistItemDto = obtainPlaylistItemDto();
        playlistItemDto.getSnippet().setVideoId("f7d9b74b");
        mockMvc.perform(post("/api/v1/playlists/items")
                        .content(objectMapper.writeValueAsString(playlistItemDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/api/v1/playlists")
                        .param("id", "fae06c8a"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.snippet.title")
                        .value("Watch Later"))
                .andExpect(jsonPath("$.snippet.description")
                        .value(nullValue()))
                .andExpect(jsonPath("$.snippet.userId")
                        .value("a05990b1"))
                .andExpect(jsonPath("$.snippet.userImageUrl")
                        .value("User 1 default thumbnail URL"))
                .andExpect(jsonPath("$.snippet.thumbnails.length()")
                        .value(1))
                .andExpect(jsonPath("$.snippet.thumbnails['DEFAULT'].url")
                        .value("Video 3 default thumbnail URL"))
                .andExpect(jsonPath("$.status.isDefaultPlaylist")
                        .value(true))
                .andExpect(jsonPath("$.status.privacy")
                        .value("PRIVATE"))
                .andExpect(jsonPath("$.contentDetails.itemCount")
                        .value(2));
    }

    @Test
    public void givenInvalidPlaylistItemDto_whenCreatePlaylistItem_thenError() throws Exception {
        // Given null playlistId
        var playlistItemDto = obtainPlaylistItemDto();
        playlistItemDto.getSnippet().setPlaylistId(null);
        mockMvc.perform(post("/api/v1/playlists/items")
                        .content(objectMapper.writeValueAsString(playlistItemDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors[0]")
                        .value("snippet.playlistId: must not be null"));

        // Given null videoId
        playlistItemDto = obtainPlaylistItemDto();
        playlistItemDto.getSnippet().setVideoId(null);
        mockMvc.perform(post("/api/v1/playlists/items")
                        .content(objectMapper.writeValueAsString(playlistItemDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors[0]")
                        .value("snippet.videoId: must not be null"));
    }

    @Test
    @WithUserDetails("user2")
    public void whenAddVideoToAnotherUserPlaylist_thenForbidden() throws Exception {
        var playlistItemDto = obtainPlaylistItemDto();
        mockMvc.perform(post("/api/v1/playlists/items")
                        .content(objectMapper.writeValueAsString(playlistItemDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    public void givenLikedVideosPlaylistItemDto_whenCreatePlaylistItem_thenError() throws Exception {
        var playlistItemDto = obtainPlaylistItemDto();
        playlistItemDto.getSnippet().setPlaylistId("c31760ea");
        mockMvc.perform(post("/api/v1/playlists/items")
                        .content(objectMapper.writeValueAsString(playlistItemDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("You cannot add items to " +
                        "Liked Videos playlist. Liked Videos playlist can only be updated when a user likes video."));
    }

    @Test
    public void givenDuplicatedPlaylistItemDto_whenCreatePlaylistItem_thenError() throws Exception {
        var playlistItemDto = obtainPlaylistItemDto();
        playlistItemDto.getSnippet().setPlaylistId("d8659362");
        playlistItemDto.getSnippet().setVideoId("f7d9b74b");

        mockMvc.perform(post("/api/v1/playlists/items")
                        .content(objectMapper.writeValueAsString(playlistItemDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message")
                        .value("The video already exists in this playlist."));
    }

    @Test
    @Transactional
    public void givenPlaylistIdAndVideoId_whenDeletePlaylistItem_thenSuccess() throws Exception {
        var playlistId = "d8659362";
        var videoId = "f7d9b74b";

        mockMvc.perform(delete("/api/v1/playlists/items")
                        .param("playlistId", playlistId)
                        .param("videoId", videoId))
                .andExpect(status().isNoContent());

        // Assert the database is updated.
        assertThat(playlistItemRepository.count()).isEqualTo(2);
        assertThat(playlistItemRepository.findAll().stream()
                .map(playlistItem -> playlistItem.getId().getPlaylistId() + ":" + playlistItem.getId().getVideoId()))
                .containsExactlyInAnyOrder("d8659362:e65707b4", "236e2aa6:37b32dc2");
    }

    @Test
    public void givenInvalidPlaylistIdAndVideoId_whenRemoveFromPlaylist_thenError() throws Exception {
        var playlistId = "d8659362";
        var videoId = "37b32dc2";

        mockMvc.perform(delete("/api/v1/playlists/items")
                        .param("playlistId", playlistId)
                        .param("videoId", videoId))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message")
                        .value("Playlist item not found."));
    }

    @Test
    @Transactional
    public void givenVideoIdFromLikedVideosPlaylist_whenRemoveFromPlaylist_thenSuccess() throws Exception {
        // First like a video
        var videoId = "f7d9b74b";
        mockMvc.perform(post("/api/v1/videos/rate/mine")
                        .param("videoId", videoId)
                        .param("rating", "like"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.rating").value(VideoRatingDto.LIKE));

        // Remove video from Liked Videos playlist
        var playlistId = "c31760ea";
        mockMvc.perform(delete("/api/v1/playlists/items")
                        .param("playlistId", playlistId)
                        .param("videoId", videoId))
                .andExpect(status().isNoContent());

        // Get video rating
        mockMvc.perform(get("/api/v1/videos/rate/mine")
                        .param("videoId", videoId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.rating").value(VideoRatingDto.NONE))
                .andExpect(jsonPath("$.publishedAt").doesNotExist());
    }

    @Test
    @WithUserDetails("user2")
    public void givenPlaylistIdVideoIdAndInvalidUserCredential_whenRemoveFromPlaylist_thenForbidden() throws Exception {
        var playlistId = "d8659362";
        var videoId = "f7d9b74b";

        mockMvc.perform(delete("/api/v1/playlists/items")
                        .param("playlistId", playlistId)
                        .param("videoId", videoId))
                .andExpect(status().isForbidden());
    }

    @Test
    public void whenPlaylistId_whenGetItems_thenSuccess() throws Exception {
        var playlistId = "d8659362";

        mockMvc.perform(get("/api/v1/playlists/items")
                        .param("playlistId", playlistId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalElements").value(2))

                .andExpect(jsonPath("$.items[0].snippet.playlistId")
                        .value("d8659362"))
                .andExpect(jsonPath("$.items[0].snippet.videoId")
                        .value("f7d9b74b"))
                .andExpect(jsonPath("$.items[0].snippet.title")
                        .value("Video 2"))
                .andExpect(jsonPath("$.items[0].snippet.description")
                        .value("Video 2 description"))
                .andExpect(jsonPath("$.items[0].snippet.videoUrl")
                        .value("Video 2 URL"))
                .andExpect(jsonPath("$.items[0].snippet.thumbnails.length()")
                        .value(1))
                .andExpect(jsonPath("$.items[0].snippet.thumbnails['DEFAULT'].url")
                        .value("Video 2 default thumbnail URL"))
                .andExpect(jsonPath("$.items[0].snippet.priority")
                        .value(0))
                .andExpect(jsonPath("$.items[0].snippet.videoOwnerUsername")
                        .value("user1"))
                .andExpect(jsonPath("$.items[0].snippet.videoOwnerUserId")
                        .value("a05990b1"))
                .andExpect(jsonPath("$.items[0].contentDetails.duration")
                        .value("PT33M20S"))

                .andExpect(jsonPath("$.items[1].snippet.playlistId")
                        .value("d8659362"))
                .andExpect(jsonPath("$.items[1].snippet.videoId")
                        .value("e65707b4"))
                .andExpect(jsonPath("$.items[1].snippet.title")
                        .value("Video 3"))
                .andExpect(jsonPath("$.items[1].snippet.description")
                        .value("Video 3 description"))
                .andExpect(jsonPath("$.items[1].snippet.videoUrl")
                        .value("Video 3 URL"))
                .andExpect(jsonPath("$.items[1].snippet.thumbnails.length()")
                        .value(1))
                .andExpect(jsonPath("$.items[1].snippet.thumbnails['DEFAULT'].url")
                        .value("Video 3 default thumbnail URL"))
                .andExpect(jsonPath("$.items[1].snippet.priority")
                        .value(1))
                .andExpect(jsonPath("$.items[1].snippet.videoOwnerUsername")
                        .value("user2"))
                .andExpect(jsonPath("$.items[1].snippet.videoOwnerUserId")
                        .value("9b79f4ba"))
                .andExpect(jsonPath("$.items[1].contentDetails.duration")
                        .value("PT50M"));
    }

    @Test
    public void givenPlaylistId_whenGetPlaylistItemVideos_thenSuccess() throws Exception {
        var playlistId = "d8659362";
        mockMvc.perform(get("/api/v1/playlists/items/videos")
                        .param("playlistId", playlistId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalElements").value(2))

                .andExpect(jsonPath("$.items[0].id").value("f7d9b74b"))
                .andExpect(jsonPath("$.items[1].id").value("e65707b4"));
    }
}