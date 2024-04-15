package com.example.videosharingapi.payload;

import jakarta.validation.constraints.Min;

public record VideoStatisticDto(
        @Min(0)
        long viewCount,
        @Min(0)
        long likeCount,
        @Min(0)
        long dislikeCount,
        @Min(0)
        long commentCount,
        @Min(0)
        long downloadCount
) {
}
