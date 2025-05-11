package com.graduationproject.auth_service.service;

import com.graduationproject.auth_service.dto.request.LoginRequestDTO;
import com.graduationproject.auth_service.dto.request.RefreshTokenRequestDTO;
import com.graduationproject.auth_service.dto.request.SaveAuthInfoRequestDTO;
import com.graduationproject.auth_service.dto.response.TokenResponseDTO;

public interface AuthService {
    TokenResponseDTO login(LoginRequestDTO request);
    TokenResponseDTO refreshToken(RefreshTokenRequestDTO request);
    void saveAuthInfo(SaveAuthInfoRequestDTO request);
} 
