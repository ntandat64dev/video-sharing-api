package com.example.videosharingapi.service.impl;

import com.example.videosharingapi.config.security.AuthenticatedUser;
import com.example.videosharingapi.dto.response.AuthResponse;
import com.example.videosharingapi.exception.AppException;
import com.example.videosharingapi.exception.ErrorCode;
import com.example.videosharingapi.service.AuthService;
import com.example.videosharingapi.service.UserService;
import com.example.videosharingapi.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    private final UserService userService;
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
        userService.createUser(username, password);
    }
}
