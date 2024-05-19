package com.example.videosharingapi.service.impl;

import com.example.videosharingapi.dto.NotificationDto;
import com.example.videosharingapi.dto.response.PageResponse;
import com.example.videosharingapi.entity.FcmMessageToken;
import com.example.videosharingapi.entity.Notification;
import com.example.videosharingapi.entity.NotificationObject;
import com.example.videosharingapi.exception.AppException;
import com.example.videosharingapi.exception.ErrorCode;
import com.example.videosharingapi.mapper.NotificationMapper;
import com.example.videosharingapi.mapper.ThumbnailMapper;
import com.example.videosharingapi.repository.*;
import com.example.videosharingapi.service.NotificationService;
import com.example.videosharingapi.util.MessageUtil;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.MulticastMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {
    private static final String TITLE = "title";
    private static final String BODY = "body";
    private static final String IMAGE = "image";
    private static final String LARGE_ICON = "large_icon";

    private final UserRepository userRepository;
    private final VideoRepository videoRepository;
    private final FollowRepository followRepository;
    private final NotificationRepository notificationRepository;
    private final NotificationObjectRepository notificationObjectRepository;
    private final FcmMessageTokenRepository fcmMessageTokenRepository;

    private final ThumbnailMapper thumbnailMapper;
    private final NotificationMapper notificationMapper;
    private final FirebaseMessaging firebaseMessaging;

    @Override
    public void pushMessage(List<String> tokens, Map<String, String> data) {
        try {
            var message = MulticastMessage.builder()
                    .addAllTokens(tokens)
                    .putAllData(data)
                    .build();
            var batchResponse = firebaseMessaging.sendEachForMulticast(message);
            if (batchResponse.getFailureCount() > 0) {
                var responses = batchResponse.getResponses();
                var failedTokens = new ArrayList<String>();
                for (int i = 0; i < responses.size(); i++) {
                    if (!responses.get(i).isSuccessful()) {
                        // The order of responses corresponds to the order of the registration tokens.
                        failedTokens.add(tokens.get(i));
                    }
                }
                // Delete all invalid tokens.
                fcmMessageTokenRepository.deleteAllByTokenIn(failedTokens);
            }
        } catch (FirebaseMessagingException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    @Transactional
    public void createNotification(NotificationDto notificationDto) {
        var objectType = notificationDto.getSnippet().getObjectType();
        if (objectType.equals(NotificationObject.ObjectType.VIDEO)) {
            createVideoNotification(notificationDto);
        }
    }

    private void createVideoNotification(NotificationDto notificationDto) {
        var actionType = notificationDto.getSnippet().getActionType();
        var objectType = notificationDto.getSnippet().getObjectType();
        var actor = userRepository.findById(notificationDto.getSnippet().getActorId()).orElseThrow();
        var video = videoRepository.findById(notificationDto.getSnippet().getObjectId()).orElseThrow();

        var follows = followRepository
                .findAllByUserId(actor.getId(), Pageable.unpaged())
                .getContent();
        if (follows.isEmpty()) {
            // If there are no followers then return.
            return;
        }

        // Create and save NotificationObject.
        var notificationObject = NotificationObject.builder()
                .actionType(actionType)
                .objectType(objectType)
                .objectId(video.getId())
                .publishedAt(LocalDateTime.now())
                .message(buildNotificationMessage(actor.getUsername(), video.getTitle(), actionType))
                .build();
        notificationObjectRepository.save(notificationObject);

        // Create and save Notifications.
        List<Notification> notifications = new ArrayList<>();
        for (var follow : follows) {
            var recipientId = follow.getFollower();
            var notification = Notification.builder()
                    .notificationObject(notificationObject)
                    .recipient(recipientId)
                    .actor(follow.getUser())
                    .isRead(false)
                    .isSeen(false)
                    .build();
            notifications.add(notification);
        }
        notificationRepository.saveAll(notifications);

        // Push message.
        var userIds = follows.stream()
                .map(follow -> follow.getFollower().getId())
                .toList();
        var tokens = fcmMessageTokenRepository.findAllByUserIdIn(userIds).stream()
                .map(FcmMessageToken::getToken)
                .toList();
        var data = new HashMap<String, String>();
        data.put(TITLE, actor.getUsername());
        data.put(BODY, video.getTitle());
        data.put(IMAGE, thumbnailMapper.getDefaultThumbnailUrl(video.getThumbnails()));
        data.put(LARGE_ICON, thumbnailMapper.getDefaultThumbnailUrl(actor.getThumbnails()));
        pushMessage(tokens, data);
    }

    private String buildNotificationMessage(String actor, String content, int actionType) {
        return switch (actionType) {
            case 1 -> MessageUtil.decode("notification.message.type.1", actor, content);
            case 2 -> MessageUtil.decode("notification.message.type.2", actor);
            case 3 -> MessageUtil.decode("notification.message.type.3", actor);
            case 4 -> MessageUtil.decode("notification.message.type.4", actor, content);
            case 5 -> MessageUtil.decode("notification.message.type.5", actor, content);
            default -> throw new AppException(ErrorCode.SOMETHING_WENT_WRONG);
        };
    }

    @Override
    @Transactional
    public void registerMessageToken(String userId, String token) {
        if (fcmMessageTokenRepository.existsByToken(token)) return;

        var user = userRepository.findById(userId).orElseThrow();
        var fcmMessageToken = FcmMessageToken.builder()
                .token(token)
                .timestamp(LocalDateTime.now())
                .user(user)
                .build();
        fcmMessageTokenRepository.save(fcmMessageToken);
    }

    @Override
    @Transactional
    public void deleteRelatedNotifications(String objectId) {
        notificationObjectRepository.findByObjectId(objectId).ifPresent(notificationObjectRepository::delete);
    }

    @Override
    @Transactional
    public void readNotification(String userId, String notificationId) {
        var notification = notificationRepository.findByIdAndRecipientId(notificationId, userId);
        notification.setIsRead(true);
        notificationRepository.save(notification);
    }

    @Override
    public int countUnseenNotifications(String userId) {
        return notificationRepository.countByRecipientIdAndIsSeenIsFalse(userId);
    }

    @Override
    @Transactional
    public PageResponse<NotificationDto> getNotificationsByUserId(String userId, Pageable pageable) {
        // Mark all notification as seen.
        notificationRepository.markAllAsSeen(userId);

        var notificationDtoPage = notificationRepository
                .findByRecipientId(userId, pageable)
                .map(notificationMapper::toNotificationDto);
        return new PageResponse<>(notificationDtoPage);
    }
}
