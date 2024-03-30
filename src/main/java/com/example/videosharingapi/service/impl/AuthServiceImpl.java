package com.example.videosharingapi.service.impl;

import com.example.videosharingapi.exception.ApplicationException;
import com.example.videosharingapi.model.entity.Channel;
import com.example.videosharingapi.model.entity.User;
import com.example.videosharingapi.payload.ChannelDto;
import com.example.videosharingapi.payload.UserDto;
import com.example.videosharingapi.payload.request.AuthRequest;
import com.example.videosharingapi.payload.response.AuthResponse;
import com.example.videosharingapi.repository.ChannelRepository;
import com.example.videosharingapi.repository.UserRepository;
import com.example.videosharingapi.service.AuthService;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Locale;

@Service
@Transactional
public class AuthServiceImpl implements AuthService {
    private final UserRepository userRepository;
    private final ChannelRepository channelRepository;

    private final MessageSource messageSource;

    public AuthServiceImpl(UserRepository userRepository, ChannelRepository channelRepository, MessageSource messageSource) {
        this.userRepository = userRepository;
        this.channelRepository = channelRepository;
        this.messageSource = messageSource;
    }

    @Override
    @Transactional(readOnly = true)
    public AuthResponse signIn(AuthRequest request) {
        var user = userRepository.findByEmailAndPassword(request.email(), request.password());
        if (user == null) {
            throw new ApplicationException(HttpStatus.BAD_REQUEST,
                    messageSource.getMessage("exception.email-password.incorrect", null, LocaleContextHolder.getLocale()));
        }
        var channel = channelRepository.findByUserId(user.getId());
        var channelDto = new ChannelDto(channel.getId(), channel.getName(),
                channel.getDescription(), channel.getPictureUrl(), channel.getJoinDate());
        var userDto = new UserDto(
                user.getId(),
                user.getEmail(),
                user.getDateOfBirth(),
                user.getPhoneNumber(),
                user.getGender(),
                user.getCountry(),
                channelDto
        );
        return new AuthResponse(messageSource.getMessage("message.login-success", null, LocaleContextHolder.getLocale()), userDto);
    }

    @Override
    public AuthResponse signUp(AuthRequest request) {
        if (userRepository.existsByEmail(request.email())) {
            throw new ApplicationException(HttpStatus.BAD_REQUEST,
                    messageSource.getMessage("exception.email.exist", null, LocaleContextHolder.getLocale()));
        }
        var user = User.builder()
                .email(request.email())
                .password(request.password())
                .build();
        var newUser = userRepository.save(user);
        var newChannel = createChannel(newUser);
        var channelDto = new ChannelDto(newChannel.getId(), newChannel.getName(),
                newChannel.getDescription(), newChannel.getPictureUrl(), newChannel.getJoinDate());
        var userDto = new UserDto(
                user.getId(),
                user.getEmail(),
                user.getDateOfBirth(),
                user.getPhoneNumber(),
                user.getGender(),
                user.getCountry(),
                channelDto
        );
        return new AuthResponse(messageSource.getMessage("message.signup-success", null, LocaleContextHolder.getLocale()), userDto);
    }

    private Channel createChannel(User user) {
        var channel = new Channel();
        channel.setName(user.getEmail());
        channel.setJoinDate(LocalDateTime.now());
        channel.setPictureUrl("/default_avatar.png");
        channel.setUser(user);
        return channelRepository.save(channel);
    }
}
