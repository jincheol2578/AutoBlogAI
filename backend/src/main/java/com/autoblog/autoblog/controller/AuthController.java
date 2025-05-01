package com.autoblog.autoblog.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.autoblog.autoblog.dto.JwtResponseDto;
import com.autoblog.autoblog.dto.LoginDto;
import com.autoblog.autoblog.dto.RegisterDto;
import com.autoblog.autoblog.security.JwtTokenProvider;
import com.autoblog.autoblog.service.AuthService;
import com.autoblog.autoblog.service.UserService;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;
    private final AuthService authService;
    
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterDto dto) {
        try {
            userService.registerUser(dto);
            return ResponseEntity.ok("User registered successfully.");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginDto dto) {
        try {
            String token = authService.login(dto);
            return ResponseEntity.ok(new JwtResponseDto(token));
        } catch (RuntimeException e) {
            return ResponseEntity.status(401).body("Invalid credentials.");
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletRequest request) {
        try {
            authService.logout();
            return ResponseEntity.ok("User logged out successfully.");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}