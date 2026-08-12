package com.vigil.controller;

import com.vigil.dto.AuthRequest;
import com.vigil.dto.AuthResponse;
import com.vigil.dto.ChangePasswordRequest;
import com.vigil.dto.RegisterRequest;
import com.vigil.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        AuthResponse response = authService.register(request);
        if (response.isSuccess()) {
            return ResponseEntity.ok(response);
        }
        return ResponseEntity.badRequest().body(response);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody AuthRequest request) {
        try {
            AuthResponse response = authService.login(request);
            if (response.isSuccess()) {
                return ResponseEntity.ok(response);
            }
            return ResponseEntity.badRequest().body(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(AuthResponse.builder()
                    .success(false)
                    .message("Invalid credentials")
                    .build());
        }
    }

    @PutMapping("/change-password")
    public ResponseEntity<AuthResponse> changePassword(@Valid @RequestBody ChangePasswordRequest request) {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (!(principal instanceof org.springframework.security.core.userdetails.User)) {
            return ResponseEntity.status(401).body(AuthResponse.builder()
                    .success(false)
                    .message("You must be logged in to change your password")
                    .build());
        }

        String email = ((org.springframework.security.core.userdetails.User) principal).getUsername();
        AuthResponse response = authService.changePassword(email, request.getCurrentPassword(), request.getNewPassword());

        if (response.isSuccess()) {
            return ResponseEntity.ok(response);
        }
        return ResponseEntity.badRequest().body(response);
    }
}

