package com.example.videosharingapi.payload.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record AuthRequest(
        @Email(message = "Invalid email format")
        @NotBlank(message = "Email is required")
        String email,
        @Size(min = 8, message = "Password must be at least 8 characters")
        String password
) {
}
