package com.example.videosharingapi.payload;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.util.UUID;

public record UserDto(
        @NotBlank(message = "{validation.user.id.required}")
        UUID id,
        @NotBlank(message = "{validation.user.email.required}")
        String email,
        LocalDate dateOfBirth,
        String phoneNumber,
        Integer gender,
        String country,
        @NotNull(message = "{validation.user.channel.required}")
        ChannelDto channel
) {
}