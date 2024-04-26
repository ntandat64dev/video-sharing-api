package com.example.videosharingapi.config.validation;

import com.example.videosharingapi.dto.FollowDto;
import com.example.videosharingapi.repository.FollowRepository;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.beans.factory.annotation.Autowired;

public class FollowNotExistsValidator implements ConstraintValidator<FollowNotExistsConstraint, FollowDto> {

    private @Autowired FollowRepository followRepository;

    @Override
    public boolean isValid(FollowDto value, ConstraintValidatorContext context) {
        return !followRepository.existsByUserIdAndFollowerId(
                value.getSnippet().getUserId(),
                value.getFollowerSnippet().getUserId()
        );
    }
}
