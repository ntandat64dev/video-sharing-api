package com.example.videosharingapi.service.impl;

import com.example.videosharingapi.dto.NotificationDto;
import com.example.videosharingapi.dto.response.PageResponse;
import com.example.videosharingapi.entity.*;
import com.example.videosharingapi.exception.AppException;
import com.example.videosharingapi.exception.ErrorCode;
import com.example.videosharingapi.mapper.NotificationMapper;
import com.example.videosharingapi.mapper.NotificationObjectMapper;
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
    private final CommentRepository commentRepository;

    private final ThumbnailMapper thumbnailMapper;
    private final NotificationObjectMapper notificationObjectMapper;
    private final NotificationMapper notificationMapper;
    private final FirebaseMessaging firebaseMessaging;

    @Override
    public void pushMessage(List<String> tokens, Map<String, String> data) {
        if (tokens == null || tokens.isEmpty()) return;
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
        var snippet = notificationDto.getSnippet();
        switch (snippet.getObjectType()) {
            case VIDEO -> handleVideoNotificationCreation(snippet);
            case COMMENT -> handleCommentNotificationCreation(snippet);
            case FOLLOW -> handleFollowNotificationCreation(snippet);
        }
    }

    private void handleVideoNotificationCreation(NotificationDto.Snippet snippet) {
        var actor = userRepository.findById(snippet.getActorId()).orElseThrow();
        var video = videoRepository.findById(snippet.getObjectId()).orElseThrow();

        var follows = followRepository.findAllByUserId(actor.getId(), Pageable.unpaged()).getContent();
        if (follows.isEmpty()) return; // If there are no followers, then return.

        var message = buildMessage(snippet.getActionType(), actor.getUsername(), video.getTitle());
        var notificationObject = createNotificationObject(snippet, message);
        notificationObjectRepository.save(notificationObject);

        var followers = follows.stream().map(Follow::getFollower).toList();
        var notifications = followers.stream()
                .map(follower -> createNotification(notificationObject, actor, follower))
                .toList();
        notificationRepository.saveAll(notifications);

        pushMessageToFollowers(followers, actor, video);
    }

    private void handleFollowNotificationCreation(NotificationDto.Snippet snippet) {
        var actor = userRepository.findById(snippet.getActorId()).orElseThrow();
        var follow = followRepository.findById(snippet.getObjectId()).orElseThrow();

        var message = buildMessage(snippet.getActionType(), actor.getUsername(), null);
        var notificationObject = createNotificationObject(snippet, message);
        notificationObjectRepository.save(notificationObject);

        var notification = createNotification(notificationObject, follow.getFollower(), follow.getUser());
        notificationRepository.save(notification);

        pushMessageToRecipient(
                List.of(notification.getRecipient().getId()), actor.getUsername(),
                message, actor.getThumbnails(), null);
    }

    private void handleCommentNotificationCreation(NotificationDto.Snippet snippet) {
        var actor = userRepository.findById(snippet.getActorId()).orElseThrow();
        var comment = commentRepository.findById(snippet.getObjectId()).orElseThrow();

        var message = buildMessage(snippet.getActionType(), actor.getUsername(), comment.getText());
        var notificationObject = createNotificationObject(snippet, message);
        notificationObjectRepository.save(notificationObject);

        var recipient = switch (snippet.getActionType()) {
            case 3 -> comment.getVideo().getUser();
            case 4 -> comment.getParent().getUser();
            default -> throw new AppException(ErrorCode.SOMETHING_WENT_WRONG);
        };
        var notification = createNotification(notificationObject, comment.getUser(), recipient);
        notificationRepository.save(notification);

        pushMessageToRecipient(
                List.of(notification.getRecipient().getId()), actor.getUsername(), comment.getText(),
                actor.getThumbnails(), comment.getVideo().getThumbnails());
    }

    private void pushMessageToFollowers(List<User> followers, User actor, Video video) {
        var followerIds = followers.stream().map(User::getId).toList();
        pushMessageToRecipient(
                followerIds, actor.getUsername(), video.getTitle(),
                actor.getThumbnails(), video.getThumbnails());
    }

    private void pushMessageToRecipient(List<String> userIds, String title, String body,
                                        List<Thumbnail> largeIconThumbnails, List<Thumbnail> imageThumbnails) {
        var tokens = fcmMessageTokenRepository.findAllByUserIdIn(userIds).stream()
                .map(FcmMessageToken::getToken)
                .toList();
        var data = createMessageData(title, body, largeIconThumbnails, imageThumbnails);
        pushMessage(tokens, data);
    }

    private NotificationObject createNotificationObject(NotificationDto.Snippet snippet, String message) {
        var notificationObject = notificationObjectMapper.fromNotificationDtoSnippet(snippet);
        notificationObject.setMessage(message);
        return notificationObject;
    }

    private Notification createNotification(NotificationObject notificationObject, User actor, User recipient) {
        return Notification.builder()
                .notificationObject(notificationObject)
                .actor(actor)
                .recipient(recipient)
                .isRead(false)
                .isSeen(false)
                .build();
    }

    private Map<String, String> createMessageData(String title, String body,
                                                  List<Thumbnail> largeIconThumbnails,
                                                  List<Thumbnail> imageThumbnails) {
        var data = new HashMap<String, String>();
        data.put(TITLE, title);
        data.put(BODY, body);
        if (largeIconThumbnails != null)
            data.put(LARGE_ICON, thumbnailMapper.getDefaultThumbnailUrl(largeIconThumbnails));
        if (imageThumbnails != null)
            data.put(IMAGE, thumbnailMapper.getDefaultThumbnailUrl(imageThumbnails));
        return data;
    }

    private String buildMessage(int actionType, String param1, String param2) {
        return switch (actionType) {
            case 1 -> MessageUtil.decode("notification.message.type.1", param1, param2);
            case 2 -> MessageUtil.decode("notification.message.type.2", param1);
            case 3 -> MessageUtil.decode("notification.message.type.3", param1, param2);
            case 4 -> MessageUtil.decode("notification.message.type.4", param1, param2);
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
        // Mark all notifications as seen.
        notificationRepository.markAllAsSeen(userId);

        var notificationDtoPage = notificationRepository
                .findByRecipientId(userId, pageable)
                .map(notificationMapper::toNotificationDto);
        return new PageResponse<>(notificationDtoPage);
    }
}
