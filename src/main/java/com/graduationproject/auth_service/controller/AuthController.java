package com.graduationproject.auth_service.controller;

import com.graduationproject.auth_service.dto.request.LoginRequestDTO;
import com.graduationproject.auth_service.dto.request.RefreshTokenRequestDTO;
import com.graduationproject.auth_service.dto.request.SaveAuthInfoRequestDTO;
import com.graduationproject.auth_service.dto.response.TokenResponseDTO;
import com.graduationproject.auth_service.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<TokenResponseDTO> login(@Valid @RequestBody LoginRequestDTO request) {
        return ResponseEntity.ok(authService.login(request));
    }

    @PostMapping("/refresh")
    public ResponseEntity<TokenResponseDTO> refreshToken(@Valid @RequestBody RefreshTokenRequestDTO request) {
        return ResponseEntity.ok(authService.refreshToken(request));
    }

    @PostMapping("/save-auth-info")
    public ResponseEntity<Void> saveAuthInfo(@Valid @RequestBody SaveAuthInfoRequestDTO request) {
        authService.saveAuthInfo(request);
        return ResponseEntity.ok().build();
    }
} 
