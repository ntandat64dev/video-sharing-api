package com.example.videosharingapi.service.impl;

import com.example.videosharingapi.exeption.ApplicationException;
import com.example.videosharingapi.model.entity.User;
import com.example.videosharingapi.payload.dto.AuthDTO;
import com.example.videosharingapi.repository.UserRepository;
import com.example.videosharingapi.service.AuthService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
public class AuthServiceImpl implements AuthService {
    private final UserRepository userRepository;

    public AuthServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public String signIn(AuthDTO authDTO) {
        if (userRepository.findByEmailAndPassword(authDTO.getEmail(), authDTO.getPassword()) == null) {
            throw new ApplicationException(HttpStatus.BAD_REQUEST, "Email or password is incorrect!");
        }
        return "Sign in successfully!";
    }

    @Override
    public String signUp(AuthDTO authDTO) {
        if (userRepository.existsByEmail(authDTO.getEmail())) {
            throw new ApplicationException(HttpStatus.BAD_REQUEST, "Email is already exists!");
        }
        var user = User.builder()
                .email(authDTO.getEmail())
                .password(authDTO.getPassword())
                .channelName(authDTO.getEmail())
                .build();
        userRepository.save(user);
        return "Sign up successfully!";
    }
}
