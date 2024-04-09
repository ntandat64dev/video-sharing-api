package com.example.videosharingapi.payload.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
public class ViewRequest {
    @NotNull
    private UUID videoId;

    @NotNull
    private UUID userId;

    @NotNull
    @PastOrPresent
    private LocalDateTime viewedAt;

    @Min(0)
    private Integer duration;
}
