package com.example.videosharingapi.controller;

import com.example.videosharingapi.common.TestSql;
import com.example.videosharingapi.dto.CommentDto;
import com.example.videosharingapi.repository.CommentRepository;
import com.example.videosharingapi.repository.VideoStatisticRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
@TestSql
@WithUserDetails("user1")
public class CommentControllerTest {
    private @Autowired VideoStatisticRepository videoStatisticRepository;
    private @Autowired CommentRepository commentRepository;

    private final String videoId = "37b32dc2";

    private @Autowired MockMvc mockMvc;
    private @Autowired ObjectMapper objectMapper;

    private CommentDto obtainCommentDto() {
        var commentDto = new CommentDto();
        commentDto.setSnippet(CommentDto.Snippet.builder()
                .videoId(videoId)
                .authorId("3f06af63")
                .text("Great video!")
                .build());
        return commentDto;
    }

    @Test
    public void givenVideoId_whenGetComments_thenSuccess() throws Exception {
        mockMvc.perform(get("/api/v1/comments")
                        .param("videoId", videoId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value("6c3239d6"));
    }

    @Test
    @Transactional
    public void givenCommentDto_whenPostComment_thenSuccess() throws Exception {
        var commentDto = obtainCommentDto();

        var result = mockMvc.perform(post("/api/v1/comments")
                        .content(objectMapper.writeValueAsBytes(commentDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.snippet.videoId")
                        .value("37b32dc2"))
                .andExpect(jsonPath("$.snippet.authorId")
                        .value("3f06af63"))
                .andExpect(jsonPath("$.snippet.text")
                        .value("Great video!"))
                .andReturn();

        // Assert Comment is created.
        var comment = commentRepository
                .findById(JsonPath.read(result.getResponse().getContentAsString(), "$.id"));
        assertThat(comment).isNotNull();
    }

    @Test
    @Transactional
    public void givenCommentDto_whenPostComment_thenVideoStatisticIsUpdated() throws Exception {
        mockMvc.perform(post("/api/v1/comments")
                        .content(objectMapper.writeValueAsBytes(obtainCommentDto()))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());

        var videoStat = videoStatisticRepository.findById(videoId).orElseThrow();
        assertThat(videoStat.getCommentCount()).isEqualTo(3);
    }
}
