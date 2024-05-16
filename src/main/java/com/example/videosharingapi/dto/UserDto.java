package com.example.videosharingapi.dto;

import com.example.videosharingapi.entity.Thumbnail;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Data
@AllArgsConstructor
@Builder
@Setter
public final class UserDto {

    @Getter
    @Setter
    public static final class Snippet {

        private String username;

        private LocalDateTime publishedAt;

        private String email;

        private LocalDate dateOfBirth;

        private String phoneNumber;

        private Integer gender;

        private String country;

        private String bio;

        private Map<Thumbnail.Type, ThumbnailDto> thumbnails;

        private List<String> roles;
    }

    @Getter
    @Setter
    public static final class Statistic {

        private Long viewCount;

        private Long followerCount;

        private Long followingCount;

        private Long videoCount;
    }

    @NotNull
    private String id;

    private Snippet snippet;

    private Statistic statistic;
}