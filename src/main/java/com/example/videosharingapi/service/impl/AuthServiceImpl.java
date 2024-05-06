package com.example.videosharingapi.service.impl;

import com.example.videosharingapi.config.security.AuthenticatedUser;
import com.example.videosharingapi.dto.response.AuthResponse;
import com.example.videosharingapi.entity.Thumbnail;
import com.example.videosharingapi.entity.User;
import com.example.videosharingapi.exception.AppException;
import com.example.videosharingapi.exception.ErrorCode;
import com.example.videosharingapi.repository.RoleRepository;
import com.example.videosharingapi.repository.UserRepository;
import com.example.videosharingapi.service.AuthService;
import com.example.videosharingapi.util.JwtUtil;
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
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;

    @Override
    public AuthResponse login(String username, String password) {
        try {
            // Authenticate using AuthenticationManager.
            var authenticationRequest = UsernamePasswordAuthenticationToken.unauthenticated(username, password);
            var authenticationResponse = authenticationManager.authenticate(authenticationRequest);
            // If no error, therefore authentication is successful then generate JWT token and return to client.
            var token = jwtUtil.generateToken((AuthenticatedUser) authenticationResponse.getPrincipal());
            return new AuthResponse(token, jwtUtil.extractExpiration(token));
        } catch (AuthenticationException e) {
            throw new AppException(ErrorCode.USERNAME_PASSWORD_INCORRECT);
        }
    }

    @Override
    @Transactional
    public void signup(String username, String password) {
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
    }
}
