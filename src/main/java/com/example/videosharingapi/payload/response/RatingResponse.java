package com.example.videosharingapi.payload.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
public class RatingResponse {

    public enum RatingType {
        LIKE, DISLIKE, NONE
    }

    private UUID videoId;
    private RatingType rating;
    private UUID ratedBy;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private LocalDateTime ratedAt;
}
