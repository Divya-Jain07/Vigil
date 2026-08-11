package com.vigil.service;

import com.vigil.dto.AuthRequest;
import com.vigil.dto.AuthResponse;
import com.vigil.dto.RegisterRequest;
import com.vigil.model.User;
import com.vigil.repository.UserRepository;
import com.vigil.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;

    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            return AuthResponse.builder()
                    .success(false)
                    .message("Email is already registered")
                    .build();
        }

        User user = User.builder()
                .email(request.getEmail())
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .createdAt(LocalDateTime.now())
                .build();

        userRepository.save(user);

        String jwtToken = jwtUtil.generateToken(user.getEmail(), user.getId());

        return AuthResponse.builder()
                .success(true)
                .token(jwtToken)
                .message("User registered successfully")
                .build();
    }

    public AuthResponse login(AuthRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        Optional<User> userOptional = userRepository.findByEmail(request.getEmail());
        
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            String jwtToken = jwtUtil.generateToken(user.getEmail(), user.getId());
            return AuthResponse.builder()
                    .success(true)
                    .token(jwtToken)
                    .message("Login successful")
                    .build();
        }

        return AuthResponse.builder()
                .success(false)
                .message("Invalid credentials")
                .build();
    }
}
