package com.example.videosharingapi.controller;

import com.example.videosharingapi.common.TestSql;
import com.example.videosharingapi.entity.FcmMessageToken;
import com.example.videosharingapi.entity.Notification;
import com.example.videosharingapi.repository.FcmMessageTokenRepository;
import com.example.videosharingapi.repository.NotificationRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
@TestSql
@WithUserDetails("user1")
public class NotificationControllerTest {

    private @Autowired NotificationRepository notificationRepository;
    private @Autowired FcmMessageTokenRepository fcmMessageTokenRepository;
    private @Autowired MockMvc mockMvc;

    @Test
    @Transactional
    public void whenReadNotification_thenSuccess() throws Exception {
        mockMvc.perform(post("/api/v1/notifications/read/mine")
                        .param("id", "856c89bc"))
                .andExpect(status().isNoContent());

        // Assert the database is updated.
        assertThat(notificationRepository.findByIdAndRecipientId("856c89bc", "a05990b1")
                .getIsRead()).isEqualTo(true);
    }

    @Test
    public void whenCountUnseenNotifications_thenSuccess() throws Exception {
        mockMvc.perform(get("/api/v1/notifications/count-unseen/mine"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(1));
    }

    @Test
    @Transactional
    public void whenGetMyNotifications_thenSuccess() throws Exception {
        mockMvc.perform(get("/api/v1/notifications/mine"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalElements").value(1))
                .andExpect(jsonPath("$.items[0].id").value("856c89bc"))
                .andExpect(jsonPath("$.items[0].snippet.actorId").value("9b79f4ba"))
                .andExpect(jsonPath("$.items[0].snippet.recipientId").value("a05990b1"))
                .andExpect(jsonPath("$.items[0].snippet.message")
                        .value("user2 uploaded: Video 3"))
                .andExpect(jsonPath("$.items[0].snippet.isSeen").value(true))
                .andExpect(jsonPath("$.items[0].snippet.isRead").value(false))
                .andExpect(jsonPath("$.items[0].snippet.actionType").value(1))
                .andExpect(jsonPath("$.items[0].snippet.objectType").value("video"))
                .andExpect(jsonPath("$.items[0].snippet.objectId").value("e65707b4"));
    }

    @Test
    @Transactional
    public void whenGetMyNotifications_thenAllNotificationsIsSeen() throws Exception {
        mockMvc.perform(get("/api/v1/notifications/mine"))
                .andExpect(status().isOk());

        var notifications = notificationRepository.findByRecipientId("a05990b1", Pageable.unpaged());
        assertThat(notifications).allMatch(Notification::getIsSeen);
        assertThat(notifications).noneMatch(Notification::getIsRead);
    }

    @Test
    @Transactional
    public void givenToken_whenRegister_thenSuccess() throws Exception {
        var token = "abc123";
        mockMvc.perform(post("/api/v1/notifications/message-token")
                        .content(token))
                .andExpect(status().isCreated());

        // Assert the database is updated.
        assertThat(fcmMessageTokenRepository.count()).isEqualTo(3);
        assertThat(fcmMessageTokenRepository.findAll().stream().map(FcmMessageToken::getToken))
                .containsExactlyInAnyOrder("838fdc717a5480e3", "b369be84eae3622c", "abc123");
    }

    @Test
    public void givenAnExistingToken_whenRegister_thenNothingHappen() throws Exception {
        var token = "838fdc717a5480e3";
        mockMvc.perform(post("/api/v1/notifications/message-token")
                        .content(token))
                .andExpect(status().isCreated());

        // Assert the database is intact.
        assertThat(fcmMessageTokenRepository.count()).isEqualTo(2);
        assertThat(fcmMessageTokenRepository.findAll().getFirst().getToken())
                .isEqualTo("838fdc717a5480e3", "b369be84eae3622c");
    }

    @Test
    @WithUserDetails("user2")
    public void givenAnExistingTokenAndInvalidUserCredential_whenRegister_thenError() throws Exception {
        var token = "838fdc717a5480e3";
        mockMvc.perform(post("/api/v1/notifications/message-token")
                        .content(token))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("The token is already registered."));

        // Assert the database is intact.
        assertThat(fcmMessageTokenRepository.count()).isEqualTo(2);
        assertThat(fcmMessageTokenRepository.findAll().getFirst().getToken())
                .isEqualTo("838fdc717a5480e3", "b369be84eae3622c");
    }

    @Test
    @Transactional
    public void givenToken_whenUnregister_thenSuccess() throws Exception {
        var token = "838fdc717a5480e3";
        mockMvc.perform(delete("/api/v1/notifications/message-token")
                        .content(token))
                .andExpect(status().isNoContent());

        // Assert the database is updated.
        assertThat(fcmMessageTokenRepository.count()).isEqualTo(1);
        assertThat(fcmMessageTokenRepository.findAll().stream().map(FcmMessageToken::getId))
                .containsExactly("783d890e");
    }

    @Test
    @Transactional
    public void givenTokenThatDoesNotExist_whenUnregister_thenNothingHappened() throws Exception {
        var token = "12345678";
        mockMvc.perform(delete("/api/v1/notifications/message-token")
                        .content(token))
                .andExpect(status().isNoContent());

        // Assert the database is intact.
        assertThat(fcmMessageTokenRepository.count()).isEqualTo(2);
        assertThat(fcmMessageTokenRepository.findAll().stream().map(FcmMessageToken::getId))
                .containsExactly("01a14281", "783d890e");
    }
}
