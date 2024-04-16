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

    @PostMapping(value = { "/signin", "/login" })
    public ResponseEntity<UserDto> signIn(
            @Email(message = "{validation.email.invalid}")
            @NotBlank(message = "{validation.email.required}")
            String email,
            @Size(min = 8, message = "{validation.password.length}")
            String password
    ) {
        var response = authService.signIn(email, password);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping(value = { "/signup", "/register" })
    public ResponseEntity<UserDto> signUp(
            @Email(message = "{validation.email.invalid}")
            @NotBlank(message = "{validation.email.required}")
            String email,
            @Size(min = 8, message = "{validation.password.length}")
            String password
    ) {
        var response = authService.signUp(email, password);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }
}
