package com.example.videosharingapi.controller;

import com.example.videosharingapi.common.TestSql;
import com.example.videosharingapi.common.TestUtil;
import com.example.videosharingapi.dto.CommentDto;
import com.example.videosharingapi.repository.CommentRepository;
import com.example.videosharingapi.repository.VideoStatisticRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
@TestSql
public class CommentControllerTest {
    private @Autowired VideoStatisticRepository videoStatisticRepository;
    private @Autowired CommentRepository commentRepository;

    private final String videoId = "37b32dc2-b0e0-45ab-8469-1ad89a90b978";

    private @Autowired TestUtil testUtil;
    private @Autowired MockMvc mockMvc;
    private @Autowired ObjectMapper objectMapper;

    private CommentDto obtainCommentDto() {
        var commentDto = new CommentDto();
        commentDto.setSnippet(CommentDto.Snippet.builder()
                .videoId(UUID.fromString(videoId))
                .authorId(UUID.fromString("3f06af63-a93c-11e4-9797-00505690773f"))
                .text("Great video!")
                .build());
        return commentDto;
    }

    @Test
    public void givenVideoId_whenGetComments_thenReturnSuccessful() throws Exception {
        var response = new AtomicReference<CommentDto[]>();
        mockMvc.perform(get("/api/v1/comments")
                        .param("videoId", videoId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(result -> testUtil.toDto(result, response, CommentDto[].class))
                .andExpect(status().isOk());
        assertThat(Arrays.stream(response.get()).map(CommentDto::getId)).containsExactly(
                UUID.fromString("6c3239d6-5b33-461a-88dd-1e2150fb0324"),
                UUID.fromString("6b342e72-e278-482a-b1e8-c66a142b40ca")
        );
    }

    @Test
    @Transactional
    public void givenCommentDto_whenPostComment_thenReturnSuccessful() throws Exception {
        var commentDto = obtainCommentDto();

        var response = new AtomicReference<CommentDto>();
        mockMvc.perform(post("/api/v1/comments")
                        .content(objectMapper.writeValueAsBytes(commentDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(result -> testUtil.toDto(result, response, CommentDto.class))
                .andExpect(status().isCreated());

        assertThat(response.get().getSnippet().getVideoId())
                .isEqualTo(UUID.fromString("37b32dc2-b0e0-45ab-8469-1ad89a90b978"));
        assertThat(response.get().getSnippet().getAuthorId())
                .isEqualTo(UUID.fromString("3f06af63-a93c-11e4-9797-00505690773f"));
        assertThat(response.get().getSnippet().getText()).isEqualTo("Great video!");

        // Assert Comment is created.
        var comment = commentRepository.findById(response.get().getId());
        assertThat(comment).isNotNull();
    }

    @Test
    @Transactional
    public void givenCommentDto_whenPostComment_thenVideoStatisticIsUpdated() throws Exception {
        var commentDto = obtainCommentDto();

        var response = new AtomicReference<CommentDto>();
        mockMvc.perform(post("/api/v1/comments")
                        .content(objectMapper.writeValueAsBytes(commentDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(result -> testUtil.toDto(result, response, CommentDto.class))
                .andExpect(status().isCreated());

        var videoStat = videoStatisticRepository.findById(UUID.fromString(videoId)).orElseThrow();
        assertThat(videoStat.getCommentCount()).isEqualTo(3);
    }
}
