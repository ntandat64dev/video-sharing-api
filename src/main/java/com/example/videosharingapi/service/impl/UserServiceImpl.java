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
import com.example.videosharingapi.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor(onConstructor_ = @Lazy)
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    @Lazy
    private final PlaylistService playlistService;

    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;

    @Override
    @Transactional
    public void createUser(String username, String password) {
        if (userRepository.existsByUsername(username)) throw new AppException(ErrorCode.USERNAME_EXISTS);

        var roleUser = roleRepository.findByName("USER");

        var user = User.builder()
                .username(username)
                .password(passwordEncoder.encode(password))
                .publishedAt(LocalDateTime.now())
                .roles(List.of(roleUser))
                .build();

        var url = "https://ui-avatars.com/api/?name=%s&size=%s&background=0D8ABC&color=fff&rouded=true&bold=true";
        var defaultThumbnail = Thumbnail.builder()
                .type(Thumbnail.Type.DEFAULT)
                .url(url.formatted(user.getUsername(), 100))
                .width(100)
                .height(100)
                .build();

        var mediumThumbnail = Thumbnail.builder()
                .type(Thumbnail.Type.MEDIUM)
                .url(url.formatted(user.getUsername(), 200))
                .width(200)
                .height(200)
                .build();

        user.setThumbnails(List.of(defaultThumbnail, mediumThumbnail));

        userRepository.save(user);

        playlistService.createDefaultPlaylistsForUser(user);
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