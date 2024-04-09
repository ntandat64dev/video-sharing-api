package com.example.videosharingapi.payload.response;

import lombok.Builder;
import lombok.Getter;

import java.util.UUID;

@Builder
@Getter
public class ViewResponse {
    private UUID videoId;
    private UUID userId;
    private Boolean haveViewedBefore;
    private Long viewCount;
}
