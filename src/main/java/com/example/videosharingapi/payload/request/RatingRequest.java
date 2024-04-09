package com.example.videosharingapi.payload.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
public class RatingRequest {

    public enum RatingType {
        LIKE, DISLIKE, NONE
    }

    @NotNull
    private UUID videoId;

    @NotNull
    private UUID userId;

    @NotNull
    private RatingType rating;

    @NotNull
    @PastOrPresent
    private LocalDateTime ratedAt;
}