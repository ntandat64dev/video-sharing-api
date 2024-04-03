package com.example.videosharingapi.service.impl;

import com.example.videosharingapi.model.entity.Hashtag;
import com.example.videosharingapi.repository.HashtagRepository;
import com.example.videosharingapi.service.UserService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
public class UserServiceImpl implements UserService {
    private final HashtagRepository hashtagRepository;

    public UserServiceImpl(HashtagRepository hashtagRepository) {
        this.hashtagRepository = hashtagRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<String> getHashtag(UUID userId) {
        return hashtagRepository.findAllByUserId(userId).stream()
                .map(Hashtag::getTag)
                .collect(Collectors.toList());
    }
}