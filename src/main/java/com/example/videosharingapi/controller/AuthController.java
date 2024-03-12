package com.example.videosharingapi.controller;

import com.example.videosharingapi.payload.dto.AuthDTO;
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
    public ResponseEntity<String> signIn(@RequestBody AuthDTO authDTO) {
        String response = authService.signIn(authDTO);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping(value = { "/signup", "/register" })
    public ResponseEntity<String> signUp(@RequestBody AuthDTO authDTO) {
        String response = authService.signUp(authDTO);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }
}
