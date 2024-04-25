package com.example.videosharingapi.config.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Target({ ElementType.TYPE, ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Constraint(validatedBy = SelfFollowValidator.class)
public @interface SelfFollowConstraint {
    String message() default "{exception.follow.self-follow}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
