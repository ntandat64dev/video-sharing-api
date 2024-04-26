package com.example.videosharingapi.controller;

import com.example.videosharingapi.dto.UserDto;
import com.example.videosharingapi.service.AuthService;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@Validated
public class AuthController {
    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<UserDto> login(
            @Email(message = "'email' {jakarta.validation.constraints.Email.message}")
            @NotBlank(message = "'email' {jakarta.validation.constraints.NotBlank.message}")
            String email,
            @Size(min = 8, message = "'password' {jakarta.validation.constraints.Size.message}")
            String password
    ) {
        var response = authService.login(email, password);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping("/signup")
    public ResponseEntity<UserDto> signup(
            @Email(message = "'email' {jakarta.validation.constraints.Email.message}")
            @NotBlank(message = "'email' {jakarta.validation.constraints.NotBlank.message}")
            String email,
            @Size(min = 8, message = "'password' {jakarta.validation.constraints.Size.message}")
            String password
    ) {
        var response = authService.signup(email, password);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }
}
