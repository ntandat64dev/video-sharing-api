package com.example.videosharingapi.service;

import java.util.List;
import java.util.UUID;

public interface UserService {
    List<String> getHashtag(UUID userId);
}
