package com.example.videosharingapi.payload;

import java.util.UUID;

public record UserDto(UUID id, String email, String photoUrl, String channelName) {
}
