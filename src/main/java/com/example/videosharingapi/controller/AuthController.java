package com.example.videosharingapi.controller;

import com.example.videosharingapi.payload.request.AuthRequest;
import com.example.videosharingapi.payload.response.AuthResponse;
import com.example.videosharingapi.service.AuthService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping(value = { "/signin", "/login" })
    public ResponseEntity<AuthResponse> signIn(@RequestBody AuthRequest request) {
        var response = authService.signIn(request);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping(value = { "/signup", "/register" })
    public ResponseEntity<AuthResponse> signUp(@RequestBody AuthRequest request) {
        var response = authService.signUp(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }
}
