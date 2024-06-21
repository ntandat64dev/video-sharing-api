package com.example.videosharingapi.service.impl;

import com.example.videosharingapi.config.security.AuthenticatedUser;
import com.example.videosharingapi.dto.UserDto;
import com.example.videosharingapi.entity.Thumbnail;
import com.example.videosharingapi.entity.User;
import com.example.videosharingapi.exception.AppException;
import com.example.videosharingapi.exception.ErrorCode;
import com.example.videosharingapi.mapper.UserMapper;
import com.example.videosharingapi.repository.RoleRepository;
import com.example.videosharingapi.repository.UserRepository;
import com.example.videosharingapi.service.PlaylistService;
import com.example.videosharingapi.service.StorageService;
import com.example.videosharingapi.service.UserService;
import com.example.videosharingapi.util.AvatarGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor(onConstructor_ = @Lazy)
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    @Lazy
    private final PlaylistService playlistService;
    private final StorageService storageService;

    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;

    @Override
    @Transactional
    public User createUser(String username, String password) {
        if (userRepository.existsByUsername(username)) throw new AppException(ErrorCode.USERNAME_EXISTS);

        var roleUser = roleRepository.findByName("USER");

        var user = User.builder()
                .username(username)
                .password(passwordEncoder.encode(password))
                .publishedAt(LocalDateTime.now())
                .roles(List.of(roleUser))
                .build();

        var defaultThumbnail = Thumbnail.builder()
                .type(Thumbnail.Type.DEFAULT)
                .url(AvatarGenerator.getUrl(username, 100))
                .width(100)
                .height(100)
                .build();

        var mediumThumbnail = Thumbnail.builder()
                .type(Thumbnail.Type.MEDIUM)
                .url(AvatarGenerator.getUrl(username, 200))
                .width(200)
                .height(200)
                .build();

        user.setThumbnails(List.of(defaultThumbnail, mediumThumbnail));

        userRepository.save(user);

        playlistService.createDefaultPlaylistsForUser(user);
        return user;
    }

    @Override
    @Transactional
    public UserDto changeProfileImage(MultipartFile imageFile, String userId) {
        var thumbnailUrl = storageService.storeThumbnailImage(imageFile);
        if (thumbnailUrl == null) throw new AppException(ErrorCode.SOMETHING_WENT_WRONG);

        var thumbnail = new Thumbnail();
        thumbnail.setType(Thumbnail.Type.DEFAULT);
        thumbnail.setUrl(thumbnailUrl);
        thumbnail.setWidth(100);
        thumbnail.setHeight(100);

        var user = userRepository.findById(userId).orElseThrow();
        user.getThumbnails().clear();
        user.getThumbnails().add(thumbnail);

        log.info("Profile image changed: userId={}", userId);

        return userMapper.toUserDto(user);
    }

    @Override
    public UserDto getUserById(String userId) {
        return userRepository.findById(userId)
                .map(userMapper::toUserDto)
                .orElse(null);
    }

    @Override
    public AuthenticatedUser getAuthenticatedUser() {
        return (AuthenticatedUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }
}