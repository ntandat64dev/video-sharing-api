package com.example.videosharingapi.payload;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;
import java.util.UUID;

public record ChannelDto(
        @NotNull(message = "Channel ID is required")
        UUID id,
        @NotBlank(message = "Channel name is required")
        String name,
        String description,
        @NotBlank(message = "Channel picture URL is required")
        String pictureUrl,
        @NotNull(message = "Channel join date is required")
        LocalDateTime joinDate
) {
}
