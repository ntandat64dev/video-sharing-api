package com.example.videosharingapi.service.impl;

import com.example.videosharingapi.dto.UserDto;
import com.example.videosharingapi.entity.Thumbnail;
import com.example.videosharingapi.entity.User;
import com.example.videosharingapi.exception.AppException;
import com.example.videosharingapi.exception.ErrorCode;
import com.example.videosharingapi.mapper.UserMapper;
import com.example.videosharingapi.repository.UserRepository;
import com.example.videosharingapi.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    private final UserRepository userRepository;

    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;

    @Override
    @Transactional(readOnly = true)
    public UserDto login(String username, String password) {
        try {
            var authenticationRequest = new UsernamePasswordAuthenticationToken(username, password);
            authenticationManager.authenticate(authenticationRequest);
            return userMapper.toUserDto(userRepository.findByUsername(username));
        } catch (AuthenticationException e) {
            throw new AppException(ErrorCode.USERNAME_PASSWORD_INCORRECT);
        }
    }

    @Override
    public UserDto signup(String username, String password) {
        if (userRepository.existsByUsername(username)) throw new AppException(ErrorCode.USERNAME_EXISTS);

        var user = User.builder()
                .username(username)
                .password(passwordEncoder.encode(password))
                .publishedAt(LocalDateTime.now())
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
        return userMapper.toUserDto(user);
    }
}
