package com.example.videosharingapi.controller;

import com.example.videosharingapi.common.TestSql;
import com.example.videosharingapi.common.TestUtil;
import com.example.videosharingapi.dto.CommentDto;
import com.example.videosharingapi.entity.NotificationObject;
import com.example.videosharingapi.repository.CommentRepository;
import com.example.videosharingapi.repository.NotificationObjectRepository;
import com.example.videosharingapi.repository.NotificationRepository;
import com.example.videosharingapi.repository.VideoStatisticRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.nullValue;
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
    private @Autowired NotificationObjectRepository notificationObjectRepository;
    private @Autowired NotificationRepository notificationRepository;

    private @Autowired MockMvc mockMvc;
    private @Autowired ObjectMapper objectMapper;
    private @Autowired TestUtil testUtil;

    private CommentDto obtainCommentDto() {
        var commentDto = new CommentDto();
        commentDto.setSnippet(CommentDto.Snippet.builder()
                .videoId("e65707b4")
                .authorId("a05990b1")
                .text("Great video!")
                .build());
        return commentDto;
    }

    private CommentDto obtainReplyCommentDto() {
        var commentDto = new CommentDto();
        commentDto.setSnippet(CommentDto.Snippet.builder()
                .videoId("e65707b4")
                .authorId("9b79f4ba")
                .parentId("3a1e0539")
                .text("Reply: Great comment")
                .build());
        return commentDto;
    }

    @Test
    @Transactional
    public void givenCommentDto_whenPostComment_thenSuccess() throws Exception {
        var commentDto = obtainCommentDto();

        mockMvc.perform(post("/api/v1/comments")
                        .content(objectMapper.writeValueAsBytes(commentDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.snippet.videoId")
                        .value("e65707b4"))
                .andExpect(jsonPath("$.snippet.authorId")
                        .value("a05990b1"))
                .andExpect(jsonPath("$.snippet.text")
                        .value("Great video!"))
                .andReturn();
    }

    @Test
    @Transactional
    public void givenCommentDto_whenPostComment_thenDatabaseIsUpdated() throws Exception {
        mockMvc.perform(post("/api/v1/comments")
                        .content(objectMapper.writeValueAsBytes(obtainCommentDto()))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());

        // Assert Comment is created.
        assertThat(commentRepository.count()).isEqualTo(4);

        // Assert VideoStatistic is updated.
        var videoStat = videoStatisticRepository.findById("e65707b4").orElseThrow();
        assertThat(videoStat.getCommentCount()).isEqualTo(3);
    }

    @Test
    @Transactional
    public void whenPostComment_thenNotificationIsCreated() throws Exception {
        var commentDto = obtainCommentDto();

        var result = mockMvc.perform(post("/api/v1/comments")
                        .content(objectMapper.writeValueAsBytes(commentDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andReturn();

        assertThat(notificationObjectRepository.count()).isEqualTo(3);
        assertThat(notificationRepository.count()).isEqualTo(3);

        var notificationObject = notificationObjectRepository
                .findByObjectId(testUtil.json(result, "$.id"))
                .orElseThrow();
        assertThat(notificationObject.getActionType()).isEqualTo(3);
        assertThat(notificationObject.getObjectType()).isEqualTo(NotificationObject.ObjectType.COMMENT);
        assertThat(notificationObject.getMessage())
                .isEqualTo("user1 has commented on your video: Great video!");

        var notifications = notificationRepository
                .findByNotificationObjectId(notificationObject.getId(), Pageable.unpaged())
                .getContent();
        assertThat(notifications).hasSize(1);
        assertThat(notifications.getFirst().getActor().getId()).isEqualTo("a05990b1");
        assertThat(notifications.getFirst().getRecipient().getId()).isEqualTo("9b79f4ba");
        assertThat(notifications.getFirst().getIsSeen()).isEqualTo(false);
        assertThat(notifications.getFirst().getIsRead()).isEqualTo(false);
    }

    @Test
    @WithUserDetails("user2")
    public void givenInvalidUserCredential_whenPostComment_thenForbidden() throws Exception {
        var commentDto = obtainCommentDto();
        mockMvc.perform(post("/api/v1/comments")
                        .content(objectMapper.writeValueAsBytes(commentDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    @Transactional
    @WithUserDetails("user2")
    public void givenCommentDto_whenPostReply_thenSuccess() throws Exception {
        var commentDto = obtainReplyCommentDto();
        mockMvc.perform(post("/api/v1/comments")
                        .content(objectMapper.writeValueAsBytes(commentDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.snippet.videoId")
                        .value("e65707b4"))
                .andExpect(jsonPath("$.snippet.authorId")
                        .value("9b79f4ba"))
                .andExpect(jsonPath("$.snippet.parentId")
                        .value("3a1e0539"))
                .andExpect(jsonPath("$.snippet.text")
                        .value("Reply: Great comment"))
                .andReturn();
    }

    @Test
    @Transactional
    @WithUserDetails("user2")
    public void givenCommentDto_whenPostReply_thenDatabaseIsUpdated() throws Exception {
        var commentDto = obtainReplyCommentDto();
        mockMvc.perform(post("/api/v1/comments")
                        .content(objectMapper.writeValueAsBytes(commentDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andReturn();

        // Assert Comment is created.
        assertThat(commentRepository.count()).isEqualTo(4);

        // Assert VideoStatistic is updated.
        var videoStat = videoStatisticRepository.findById("e65707b4").orElseThrow();
        assertThat(videoStat.getCommentCount()).isEqualTo(3);

        // Assert statistic of comment being replied is updated.
        mockMvc.perform(get("/api/v1/comments/{id}", "3a1e0539"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statistic.likeCount").value(1))
                .andExpect(jsonPath("$.statistic.dislikeCount").value(0))
                .andExpect(jsonPath("$.statistic.replyCount").value(2));
    }

    @Test
    @Transactional
    @WithUserDetails("user2")
    public void givenCommentDto_whenPostReply_thenNotificationsAreCreated() throws Exception {
        var commentDto = obtainReplyCommentDto();
        var result = mockMvc.perform(post("/api/v1/comments")
                        .content(objectMapper.writeValueAsBytes(commentDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andReturn();

        assertThat(notificationObjectRepository.count()).isEqualTo(3);
        assertThat(notificationRepository.count()).isEqualTo(3);

        var notificationObject = notificationObjectRepository
                .findByObjectId(testUtil.json(result, "$.id"))
                .orElseThrow();
        assertThat(notificationObject.getActionType()).isEqualTo(4);
        assertThat(notificationObject.getObjectType()).isEqualTo(NotificationObject.ObjectType.COMMENT);
        assertThat(notificationObject.getMessage())
                .isEqualTo("user2 has replied to your comment: Reply: Great comment");

        var notifications = notificationRepository
                .findByNotificationObjectId(notificationObject.getId(), Pageable.unpaged())
                .getContent();
        assertThat(notifications).hasSize(1);
        assertThat(notifications.getFirst().getActor().getId()).isEqualTo("9b79f4ba");
        assertThat(notifications.getFirst().getRecipient().getId()).isEqualTo("a05990b1");
        assertThat(notifications.getFirst().getIsSeen()).isEqualTo(false);
        assertThat(notifications.getFirst().getIsRead()).isEqualTo(false);
    }

    @Test
    @WithUserDetails("user2")
    public void whenPostNestedReply_thenBadRequest() throws Exception {
        var commentDto = obtainReplyCommentDto();
        commentDto.getSnippet().setParentId("6b342e72");

        mockMvc.perform(post("/api/v1/comments")
                        .content(objectMapper.writeValueAsBytes(commentDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Nesting reply is not supported."));
    }

    @Test
    public void givenCommentId_whenGetComment_thenSuccess() throws Exception {
        mockMvc.perform(get("/api/v1/comments/{id}", "3a1e0539"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.snippet.videoId").value("e65707b4"))
                .andExpect(jsonPath("$.snippet.authorId").value("a05990b1"))
                .andExpect(jsonPath("$.snippet.text").value("[user1] Good video"))
                .andExpect(jsonPath("$.snippet.parentId").value(nullValue()))
                .andExpect(jsonPath("$.statistic.likeCount").value(1))
                .andExpect(jsonPath("$.statistic.dislikeCount").value(0))
                .andExpect(jsonPath("$.statistic.replyCount").value(1));
    }

    @Test
    public void givenVideoId_whenGetComments_thenSuccess() throws Exception {
        mockMvc.perform(get("/api/v1/comments")
                        .param("videoId", "37b32dc2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalElements").value(1))
                .andExpect(jsonPath("$.items.length()").value(1))
                .andExpect(jsonPath("$.items[0].id").value("6c3239d6"))
                .andExpect(jsonPath("$.items[0].statistic.likeCount").value(0))
                .andExpect(jsonPath("$.items[0].statistic.dislikeCount").value(1))
                .andExpect(jsonPath("$.items[0].statistic.replyCount").value(0));
    }

    @Test
    public void givenCommentId_whenGetReplies_thenSuccess() throws Exception {
        mockMvc.perform(get("/api/v1/comments/replies")
                        .param("commentId", "3a1e0539"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalElements").value(1))
                .andExpect(jsonPath("$.items.length()").value(1))
                .andExpect(jsonPath("$.items[0].id").value("6b342e72"))
                .andExpect(jsonPath("$.items[0].statistic.likeCount").value(1))
                .andExpect(jsonPath("$.items[0].statistic.dislikeCount").value(0))
                .andExpect(jsonPath("$.items[0].statistic.replyCount").value(0));

        mockMvc.perform(get("/api/v1/comments/replies")
                        .param("commentId", "6c3239d6"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalElements").value(0))
                .andExpect(jsonPath("$.items.length()").value(0))
                .andExpect(jsonPath("$.items[0]").doesNotExist());
    }
}
