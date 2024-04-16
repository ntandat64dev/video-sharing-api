package com.example.videosharingapi.payload;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ThumbnailDto {

    private String url;

    private Integer width;

    private Integer height;
}
