package com.graduationproject.auth_service.service;

import com.graduationproject.auth_service.dto.response.UserResponseDTO;

public interface JwtService {
    String generateToken(UserResponseDTO user);
    String getUsernameFromToken(String token);
    boolean validateToken(String token);
    long getExpirationTime();
} 
