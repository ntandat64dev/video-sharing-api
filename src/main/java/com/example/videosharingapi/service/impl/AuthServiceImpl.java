package com.example.videosharingapi.service.impl;

import com.example.videosharingapi.exception.ApplicationException;
import com.example.videosharingapi.model.entity.User;
import com.example.videosharingapi.payload.UserDto;
import com.example.videosharingapi.payload.request.AuthRequest;
import com.example.videosharingapi.payload.response.AuthResponse;
import com.example.videosharingapi.repository.UserRepository;
import com.example.videosharingapi.service.AuthService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthServiceImpl implements AuthService {
    private final UserRepository userRepository;

    public AuthServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public AuthResponse signIn(AuthRequest request) {
        var user = userRepository.findByEmailAndPassword(request.email(), request.password());
        if (user == null) {
            throw new ApplicationException(HttpStatus.BAD_REQUEST, "Email or password is incorrect!");
        }
        return new AuthResponse(
                "Sign in successfully.",
                new UserDto(user.getId(), user.getEmail(), user.getPhotoUrl(), user.getChannelName()));
    }

    @Override
    public AuthResponse signUp(AuthRequest request) {
        if (userRepository.existsByEmail(request.email())) {
            throw new ApplicationException(HttpStatus.BAD_REQUEST, "Email is already exists!");
        }
        var user = User.builder()
                .email(request.email())
                .password(request.password())
                .channelName(request.email())
                .build();
        var newUser = userRepository.save(user);
        return new AuthResponse(
                "Sign up successfully.",
                new UserDto(newUser.getId(), newUser.getEmail(), newUser.getPhotoUrl(), newUser.getChannelName()));
    }
}
