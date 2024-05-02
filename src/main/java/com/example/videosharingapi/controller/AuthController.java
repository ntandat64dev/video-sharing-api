package com.example.videosharingapi.controller;

import com.example.videosharingapi.dto.UserDto;
import com.example.videosharingapi.service.AuthService;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
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
    public ResponseEntity<UserDto> login(
            @Email @NotBlank String email,
            @Size(min = 8) String password
    ) {
        var response = authService.login(email, password);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping("/signup")
    public ResponseEntity<UserDto> signup(
            @Email @NotBlank String email,
            @Size(min = 8) String password
    ) {
        var response = authService.signup(email, password);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }
}
