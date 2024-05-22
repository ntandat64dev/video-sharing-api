package com.example.videosharingapi.exception;

import com.example.videosharingapi.util.MessageUtil;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

@Getter
public enum ErrorCode {
    SOMETHING_WENT_WRONG(HttpStatus.INTERNAL_SERVER_ERROR, "error.some-thing-went-wrong"),
    SELF_FOLLOW(HttpStatus.BAD_REQUEST, "error.follow.self-follow"),
    USERNAME_PASSWORD_INCORRECT(HttpStatus.BAD_REQUEST, "error.username-password.incorrect"),
    USERNAME_EXISTS(HttpStatus.BAD_REQUEST, "error.username.exist"),
    FOLLOW_EXISTS(HttpStatus.BAD_REQUEST, "error.follow.already-exist"),
    NESTED_REPLY(HttpStatus.BAD_REQUEST, "error.comment.nested-reply"),
    AUTHENTICATION_ERROR(HttpStatus.UNAUTHORIZED, "error.security.authentication-error"),
    FORBIDDEN(HttpStatus.FORBIDDEN, "error.security.forbidden"),
    ;

    ErrorCode(HttpStatusCode statusCode, String message) {
        this.statusCode = statusCode;
        this.message = MessageUtil.decode(message);
    }

    private final HttpStatusCode statusCode;
    private final String message;
}
