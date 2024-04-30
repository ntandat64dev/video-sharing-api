package com.example.videosharingapi.service.impl;

import com.example.videosharingapi.dto.UserDto;
import com.example.videosharingapi.exception.AppException;
import com.example.videosharingapi.mapper.UserMapper;
import com.example.videosharingapi.entity.Thumbnail;
import com.example.videosharingapi.entity.User;
import com.example.videosharingapi.repository.UserRepository;
import com.example.videosharingapi.service.AuthService;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class AuthServiceImpl implements AuthService {
    private final UserRepository userRepository;

    private final MessageSource messageSource;
    private final UserMapper userMapper;

    public AuthServiceImpl(UserRepository userRepository, MessageSource messageSource, UserMapper userMapper) {
        this.userRepository = userRepository;
        this.messageSource = messageSource;
        this.userMapper = userMapper;
    }

    @Override
    @Transactional(readOnly = true)
    public UserDto login(String email, String password) {
        var user = userRepository.findByEmailAndPassword(email, password);
        if (user == null) {
            throw new AppException(HttpStatus.BAD_REQUEST,
                    messageSource.getMessage("exception.email-password.incorrect", null,
                            LocaleContextHolder.getLocale()));
        }
        return userMapper.toUserDto(user);
    }

    @Override
    public UserDto signup(String email, String password) {
        if (userRepository.existsByEmail(email)) {
            throw new AppException(HttpStatus.BAD_REQUEST,
                    messageSource.getMessage("exception.email.exist", null,
                            LocaleContextHolder.getLocale()));
        }

        var user = User.builder()
                .email(email)
                .username(email.substring(0, email.indexOf('@')))
                .password(password)
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
