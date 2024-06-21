package com.example.videosharingapi.exception;

import com.example.videosharingapi.util.MessageUtil;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

@Getter
public enum ErrorCode {
    SOMETHING_WENT_WRONG(HttpStatus.INTERNAL_SERVER_ERROR, "error.some-thing-went-wrong"),
    AUTHENTICATION_ERROR(HttpStatus.UNAUTHORIZED, "error.security.authentication-error"),
    FORBIDDEN(HttpStatus.FORBIDDEN, "error.security.forbidden"),
    SELF_FOLLOW(HttpStatus.BAD_REQUEST, "error.follow.self-follow"),
    USERNAME_PASSWORD_INCORRECT(HttpStatus.BAD_REQUEST, "error.username-password.incorrect"),
    USERNAME_EXISTS(HttpStatus.BAD_REQUEST, "error.username.exist"),
    FOLLOW_EXISTS(HttpStatus.BAD_REQUEST, "error.follow.already-exist"),
    NESTED_REPLY(HttpStatus.BAD_REQUEST, "error.comment.nested-reply"),
    TOKEN_ALREADY_REGISTERED(HttpStatus.BAD_REQUEST, "error.token.already-registered"),
    UPDATE_DEFAULT_PLAYLISTS(HttpStatus.FORBIDDEN, "error.playlist.update-default-playlists"),
    DELETE_DEFAULT_PLAYLISTS(HttpStatus.FORBIDDEN, "error.playlist.delete-default-playlists"),
    ACCESS_FORBIDDEN_PLAYLIST(HttpStatus.FORBIDDEN, "error.playlist.access-forbidden"),
    PLAYLIST_ITEM_ALREADY_EXISTS(HttpStatus.BAD_REQUEST, "error.playlist.item.already-exists"),
    PLAYLIST_ITEM_NOT_FOUNT(HttpStatus.BAD_REQUEST, "error.playlist.item.not-found"),
    PLAYLIST_ITEM_ADD_TO_LIKED_VIDEOS(HttpStatus.BAD_REQUEST, "error.playlist.item.add-to-liked-videos"),
    ;

    ErrorCode(HttpStatusCode statusCode, String message) {
        this.statusCode = statusCode;
        this.message = MessageUtil.decode(message);
    }

    private final HttpStatusCode statusCode;
    private final String message;
}
