package com.example.videosharingapi.payload;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Setter;

import java.time.LocalDate;
import java.util.UUID;

@Data
@AllArgsConstructor
@Builder
@Setter
public final class UserDto {
    @NotBlank(message = "{validation.user.id.required}")
    private UUID id;
    @NotBlank(message = "{validation.user.email.required}")
    private String email;
    private LocalDate dateOfBirth;
    private String phoneNumber;
    private Integer gender;
    private String country;
    @NotNull(message = "{validation.user.channel.required}")
    private ChannelDto channel;
}