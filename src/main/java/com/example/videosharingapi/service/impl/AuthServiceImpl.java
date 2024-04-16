package com.example.videosharingapi.service.impl;

import com.example.videosharingapi.dto.UserDto;
import com.example.videosharingapi.exception.ApplicationException;
import com.example.videosharingapi.mapper.UserMapper;
import com.example.videosharingapi.model.entity.Channel;
import com.example.videosharingapi.model.entity.Thumbnail;
import com.example.videosharingapi.model.entity.User;
import com.example.videosharingapi.repository.ChannelRepository;
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
    private final ChannelRepository channelRepository;

    private final MessageSource messageSource;
    private final UserMapper userMapper;

    public AuthServiceImpl(
            UserRepository userRepository, ChannelRepository channelRepository,
            MessageSource messageSource, UserMapper userMapper
    ) {
        this.userRepository = userRepository;
        this.channelRepository = channelRepository;
        this.messageSource = messageSource;
        this.userMapper = userMapper;
    }

    @Override
    @Transactional(readOnly = true)
    public UserDto signIn(String email, String password) {
        var user = userRepository.findByEmailAndPassword(email, password);
        if (user == null) {
            throw new ApplicationException(HttpStatus.BAD_REQUEST,
                    messageSource.getMessage("exception.email-password.incorrect", null,
                            LocaleContextHolder.getLocale()));
        }
        return userMapper.toUserDto(user);
    }

    @Override
    public UserDto signUp(String email, String password) {
        if (userRepository.existsByEmail(email)) {
            throw new ApplicationException(HttpStatus.BAD_REQUEST,
                    messageSource.getMessage("exception.email.exist", null,
                            LocaleContextHolder.getLocale()));
        }
        var user = User.builder().email(email).password(password).build();
        createChannel(user);
        userRepository.save(user);
        return userMapper.toUserDto(user);
    }

    private void createChannel(User user) {
        var channel = new Channel();
        channel.setTitle(user.getEmail().substring(0, user.getEmail().indexOf('@')));
        channel.setPublishedAt(LocalDateTime.now());

        var url = "https://ui-avatars.com/api/?name=%s&size=%s&background=0D8ABC&color=fff&rouded=true&bold=true";
        var defaultThumbnail = new Thumbnail();
        defaultThumbnail.setType(Thumbnail.Type.DEFAULT);
        defaultThumbnail.setUrl(url.formatted(channel.getTitle(), 100));
        defaultThumbnail.setWidth(100);
        defaultThumbnail.setHeight(100);

        var mediumThumbnail = new Thumbnail();
        mediumThumbnail.setType(Thumbnail.Type.MEDIUM);
        mediumThumbnail.setUrl(url.formatted(channel.getTitle(), 200));
        mediumThumbnail.setWidth(200);
        mediumThumbnail.setHeight(200);

        channel.setThumbnails(List.of(defaultThumbnail, mediumThumbnail));
        channelRepository.save(channel);
        user.setChannel(channel);
    }
}
