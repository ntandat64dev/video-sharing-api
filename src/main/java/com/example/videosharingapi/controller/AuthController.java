package com.example.videosharingapi.controller;

import com.example.videosharingapi.dto.response.AuthResponse;
import com.example.videosharingapi.service.AuthService;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@Validated
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(
            @NotNull String username,
            @Size(min = 8) @NotNull String password
    ) {
        var response = authService.login(username, password);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/signup")
    public ResponseEntity<Void> signup(
            @NotNull String username,
            @Size(min = 8) @NotNull String password
    ) {
        authService.signup(username, password);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }
}
