package com.example.videosharingapi.config.validation;

import com.example.videosharingapi.entity.*;
import com.example.videosharingapi.repository.*;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;

public class IdExistsConstraintValidator implements ConstraintValidator<IdExistsConstraint, String> {

    private @Autowired UserRepository userRepository;
    private @Autowired VideoRepository videoRepository;
    private @Autowired CommentRepository commentRepository;
    private @Autowired FollowRepository followRepository;
    private @Autowired CategoryRepository categoryRepository;

    private JpaRepository<?, String> repository;

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
        } else if (constraintAnnotation.entity() == Category.class) {
            repository = categoryRepository;
        }
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (repository == null) return false;
        if (value == null) return true;
        return repository.existsById(value);
    }
}
