package com.graduationproject.auth_service.service;

import com.graduationproject.common.dto.UserResponseDTO;

public interface JwtService {
    String generateToken(UserResponseDTO user, java.util.List<String> roles);

    String getUsernameFromToken(String token);

    boolean validateToken(String token);

    long getExpirationTime();
}
