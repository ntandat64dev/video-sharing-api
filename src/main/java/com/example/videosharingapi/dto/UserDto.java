package com.example.videosharingapi.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDate;
import java.util.UUID;

@Data
@AllArgsConstructor
@Builder
@Setter
public final class UserDto {

    @Getter
    @Setter
    public static final class Snippet {

        private String email;

        private LocalDate dateOfBirth;

        private String phoneNumber;

        private Integer gender;

        private String country;

        private ChannelDto channel;
    }

    @NotBlank(message = "{validation.user.id.required}")
    private UUID id;

    private Snippet snippet;
}