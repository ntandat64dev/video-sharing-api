package com.example.videosharingapi.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Target({ ElementType.FIELD, ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Constraint(validatedBy = ValidFileValidator.class)
public @interface ValidFile {
    String message() default "{validation.video.file.invalid}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    String type();
}
