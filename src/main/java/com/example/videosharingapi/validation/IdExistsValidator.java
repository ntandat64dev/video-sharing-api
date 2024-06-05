package com.example.videosharingapi.validation;

import com.example.videosharingapi.entity.*;
import com.example.videosharingapi.repository.*;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.repository.JpaRepository;

@RequiredArgsConstructor
public class IdExistsValidator implements ConstraintValidator<IdExists, String> {

    private final UserRepository userRepository;
    private final VideoRepository videoRepository;
    private final CommentRepository commentRepository;
    private final FollowRepository followRepository;
    private final CategoryRepository categoryRepository;
    private final NotificationRepository notificationRepository;
    private final PlaylistRepository playlistRepository;

    private JpaRepository<?, String> repository;

    @Override
    public void initialize(IdExists constraintAnnotation) {
        if (constraintAnnotation.entity() == User.class) {
            repository = userRepository;
        } else if (constraintAnnotation.entity() == Video.class) {
            repository = videoRepository;
        } else if (constraintAnnotation.entity() == Comment.class) {
            repository = commentRepository;
        } else if (constraintAnnotation.entity() == Follow.class) {
            repository = followRepository;
        } else if (constraintAnnotation.entity() == Category.class) {
            repository = categoryRepository;
        } else if (constraintAnnotation.entity() == Notification.class) {
            repository = notificationRepository;
        } else if (constraintAnnotation.entity() == Playlist.class) {
            repository = playlistRepository;
        }
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (repository == null) return false;
        if (value == null) return true;
        return repository.existsById(value);
    }
}
