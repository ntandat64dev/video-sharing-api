package com.example.videosharingapi.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
public class VideoRatingDto {
    public static final String LIKE = "like";
    public static final String DISLIKE = "dislike";
    public static final String NONE = "none";

    private UUID videoId;

    private UUID userId;

    private String rating;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private LocalDateTime publishedAt;
}
