package com.example.videosharingapi.payload;

import com.example.videosharingapi.model.entity.Thumbnail;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
public class ChannelDto {
    @NotNull(message = "{validation.channel.id.required}")
    private UUID id;

    @NotBlank(message = "{validation.channel.title.required}")
    private String title;

    private String description;

    @NotNull(message = "{validation.channel.published-at.required}")
    private LocalDateTime publishedAt;

    @NotBlank(message = "{validation.channel.thumbnails.required}")
    private List<Thumbnail> thumbnails;
}