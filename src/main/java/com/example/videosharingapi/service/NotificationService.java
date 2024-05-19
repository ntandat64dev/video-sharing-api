package com.example.videosharingapi.service;

import com.example.videosharingapi.dto.NotificationDto;
import com.example.videosharingapi.dto.response.PageResponse;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;

public interface NotificationService {

    void pushMessage(List<String> tokens, Map<String, String> data);

    void createNotification(NotificationDto notificationDto);

    void registerMessageToken(String userId, String token);

    void deleteRelatedNotifications(String objectId);

    void readNotification(String userId, String notificationId);

    int countUnseenNotifications(String userId);

    PageResponse<NotificationDto> getNotificationsByUserId(String userId, Pageable pageable);
}
