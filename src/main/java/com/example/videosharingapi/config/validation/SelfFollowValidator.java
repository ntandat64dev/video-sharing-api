package com.example.videosharingapi.config.validation;

import com.example.videosharingapi.dto.FollowDto;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class SelfFollowValidator implements ConstraintValidator<SelfFollowConstraint, FollowDto> {
    @Override
    public boolean isValid(FollowDto value, ConstraintValidatorContext context) {
        return !value.getSnippet().getUserId().equals(value.getFollowerSnippet().getUserId());
    }
}
