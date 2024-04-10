package com.example.videosharingapi.payload;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
public class CommentDto {
    private UUID id;
    private UUID videoId;
    private String content;
    private UUID parentId;
    private LocalDateTime commentedAt;
    private UUID commentedBy;
}
