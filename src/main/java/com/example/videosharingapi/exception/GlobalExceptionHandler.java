package com.example.videosharingapi.exception;

import com.example.videosharingapi.dto.response.ErrorResponse;
import jakarta.validation.ConstraintViolationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.ArrayList;

@RestControllerAdvice
@RequiredArgsConstructor
@Slf4j
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @Override
    protected ResponseEntity<Object> handleExceptionInternal(
            Exception ex,
            Object body,
            @NotNull HttpHeaders headers,
            @NotNull HttpStatusCode statusCode,
            @NotNull WebRequest request
    ) {
        var errorResponse = ErrorResponse.builder()
                .httpStatus(statusCode)
                .message(ex.getLocalizedMessage())
                .build();
        return new ResponseEntity<>(errorResponse, errorResponse.getHttpStatus());
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex,
            @NotNull HttpHeaders headers,
            @NotNull HttpStatusCode status,
            @NotNull WebRequest request
    ) {
        var errors = new ArrayList<String>();
        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            errors.add(String.format("%s: %s", error.getField(), error.getDefaultMessage()));
        }
        for (ObjectError error : ex.getBindingResult().getGlobalErrors()) {
            errors.add(String.format("%s: %s", error.getObjectName(), error.getDefaultMessage()));
        }
        var errorResponse = ErrorResponse.builder()
                .httpStatus(HttpStatus.BAD_REQUEST)
                .message(ex.getLocalizedMessage())
                .errors(errors)
                .build();
        return new ResponseEntity<>(errorResponse, errorResponse.getHttpStatus());
    }

    /**
     * Thrown when using method-level validation with {@link Validated}.
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponse> handleConstraintViolationException(ConstraintViolationException ex) {
        var errors = new ArrayList<String>();
        for (var violation : ex.getConstraintViolations()) {
            var propertyPath = violation.getPropertyPath().toString();
            var fieldName = propertyPath.substring(propertyPath.lastIndexOf(".") + 1);
            errors.add(String.format("%s: %s", fieldName, violation.getMessage()));
        }
        var errorResponse = ErrorResponse.builder()
                .httpStatus(HttpStatus.BAD_REQUEST)
                .message(ex.getLocalizedMessage())
                .errors(errors)
                .build();
        return new ResponseEntity<>(errorResponse, errorResponse.getHttpStatus());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGlobalException(Exception ex) {
        var errorResponse = ErrorResponse.builder()
                .httpStatus(HttpStatus.INTERNAL_SERVER_ERROR)
                .message(ex.getLocalizedMessage())
                .build();
        return new ResponseEntity<>(errorResponse, errorResponse.getHttpStatus());
    }

    @ExceptionHandler(AppException.class)
    public ResponseEntity<ErrorResponse> handleAppException(AppException ex) {
        var errorCode = ex.getErrorCode();
        return ResponseEntity
                .status(errorCode.getStatusCode())
                .body(ErrorResponse.builder()
                        .httpStatus(errorCode.getStatusCode())
                        .message(errorCode.getMessage())
                        .build());
    }
}
