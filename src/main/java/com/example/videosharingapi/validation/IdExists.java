package com.example.videosharingapi.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

/**
 * The ID of {@code entity} must be existing in the database.
 * <p>
 * {@code null} value is considered valid.
 */
@Target({ ElementType.FIELD, ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Constraint(validatedBy = IdExistsValidator.class)
public @interface IdExists {
    String message() default "{validation.does-not-exist}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    Class<?> entity();
}
