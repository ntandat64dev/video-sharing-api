package com.example.videosharingapi.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public final class ThumbnailDto {
    private String url;
    private Integer width;
    private Integer height;
}
