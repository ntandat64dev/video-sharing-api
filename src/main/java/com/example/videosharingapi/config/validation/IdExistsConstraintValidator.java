package com.example.videosharingapi.config.validation;

import com.example.videosharingapi.model.entity.Comment;
import com.example.videosharingapi.model.entity.Follow;
import com.example.videosharingapi.model.entity.User;
import com.example.videosharingapi.model.entity.Video;
import com.example.videosharingapi.repository.CommentRepository;
import com.example.videosharingapi.repository.FollowRepository;
import com.example.videosharingapi.repository.UserRepository;
import com.example.videosharingapi.repository.VideoRepository;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public class IdExistsConstraintValidator implements ConstraintValidator<IdExistsConstraint, UUID> {

    private @Autowired UserRepository userRepository;
    private @Autowired VideoRepository videoRepository;
    private @Autowired CommentRepository commentRepository;
    private @Autowired FollowRepository followRepository;

    private JpaRepository<?, UUID> repository;

    @Override
    public void initialize(IdExistsConstraint constraintAnnotation) {
        if (constraintAnnotation.entity() == User.class) {
            repository = userRepository;
        } else if (constraintAnnotation.entity() == Video.class) {
            repository = videoRepository;
        } else if (constraintAnnotation.entity() == Comment.class) {
            repository = commentRepository;
        } else if (constraintAnnotation.entity() == Follow.class) {
            repository = followRepository;
        }
    }

    @Override
    public boolean isValid(UUID value, ConstraintValidatorContext context) {
        if (repository == null) return false;
        if (value == null) return true;
        return repository.existsById(value);
    }
}
