package com.example.videosharingapi.controller;

import com.example.videosharingapi.config.security.AuthenticatedUser;
import com.example.videosharingapi.dto.NotificationDto;
import com.example.videosharingapi.dto.response.PageResponse;
import com.example.videosharingapi.entity.Notification;
import com.example.videosharingapi.service.NotificationService;
import com.example.videosharingapi.validation.IdExists;
import jakarta.validation.constraints.NotEmpty;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/notifications")
@RequiredArgsConstructor
@Validated
public class NotificationController {

    private final NotificationService notificationService;

    @PostMapping("/read/mine")
    public ResponseEntity<?> readNotification(
            @AuthenticationPrincipal AuthenticatedUser user,
            @IdExists(entity = Notification.class) String id
    ) {
        notificationService.readNotification(user.getUserId(), id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PostMapping("/message-token")
    public ResponseEntity<?> registerMessageToken(
            @AuthenticationPrincipal AuthenticatedUser user,
            @RequestBody @NotEmpty String token
    ) {
        notificationService.registerMessageToken(user.getUserId(), token);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @DeleteMapping("/message-token")
    public ResponseEntity<?> unregisterMessageToken(
            @AuthenticationPrincipal AuthenticatedUser user,
            @RequestBody @NotEmpty String token
    ) {
        notificationService.unregisterMessageToken(user.getUserId(), token);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping("/count-unseen/mine")
    public ResponseEntity<Integer> countUnseenNotification(@AuthenticationPrincipal AuthenticatedUser user) {
        var response = notificationService.countUnseenNotifications(user.getUserId());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/mine")
    public ResponseEntity<PageResponse<NotificationDto>> getMyNotifications(
            @AuthenticationPrincipal
            AuthenticatedUser user,
            @PageableDefault(sort = "notificationObject.publishedAt", direction = Sort.Direction.DESC)
            Pageable pageable
    ) {
        var response = notificationService.getNotificationsByUserId(user.getUserId(), pageable);
        return ResponseEntity.ok(response);
    }
}
