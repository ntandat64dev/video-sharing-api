package com.example.videosharingapi.dto.response;

import lombok.Builder;

@Builder
public record SearchResponse(
        SearchType type,
        Object snippet
) {
    public enum SearchType {
        VIDEO, USER, PLAYLIST
    }
}