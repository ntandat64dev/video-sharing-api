package com.example.videosharingapi.config.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Target({ ElementType.FIELD, ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Constraint(validatedBy = FileUploadValidator.class)
public @interface FileUploadConstraint {
    String message() default "{validation.video.file.invalid}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
