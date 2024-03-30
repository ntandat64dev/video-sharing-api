package com.example.videosharingapi.payload;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;
import java.util.UUID;

public record ChannelDto(
        @NotNull(message = "{validation.channel.id.required}")
        UUID id,
        @NotBlank(message = "{validation.channel.name.required}")
        String name,
        String description,
        @NotBlank(message = "{validation.channel.picture-url.required}")
        String pictureUrl,
        @NotNull(message = "{validation.channel.join-date.required}")
        LocalDateTime joinDate
) {
}
