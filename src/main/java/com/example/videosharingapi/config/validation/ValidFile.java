package com.example.videosharingapi.config.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Target({ ElementType.METHOD, ElementType.FIELD, ElementType.CONSTRUCTOR, ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = FileUploadValidator.class)
public @interface ValidFile {
    String message() default "{validation.video.file.invalid}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
