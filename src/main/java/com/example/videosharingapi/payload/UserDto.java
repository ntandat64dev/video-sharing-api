package com.example.videosharingapi.payload;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.util.UUID;

public record UserDto(
        @NotBlank(message = "User ID is required")
        UUID id,
        @NotBlank(message = "User email is required")
        String email,
        LocalDate dateOfBirth,
        String phoneNumber,
        Integer gender,
        String country,
        @NotNull(message = "User's channel is required")
        ChannelDto channel
) {
}