package com.example.videosharingapi.config.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Target({ ElementType.TYPE, ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Constraint(validatedBy = FollowExistsValidator.class)
public @interface FollowExistsConstraint {
    String message() default "{exception.follow.already-exist}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
