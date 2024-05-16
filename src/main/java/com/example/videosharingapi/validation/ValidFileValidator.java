package com.example.videosharingapi.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.web.multipart.MultipartFile;

public class ValidFileValidator implements ConstraintValidator<ValidFile, MultipartFile> {
    private String type;

    @Override
    public void initialize(ValidFile constraintAnnotation) {
        type = constraintAnnotation.type();
    }

    @Override
    public boolean isValid(MultipartFile value, ConstraintValidatorContext context) {
        return !value.isEmpty() &&
                value.getContentType() != null &&
                value.getContentType().contains(type);
    }
}
