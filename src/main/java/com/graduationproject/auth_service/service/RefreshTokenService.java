package com.graduationproject.auth_service.service;

import com.graduationproject.auth_service.entity.AuthUser;

public interface RefreshTokenService {
    AuthUser createRefreshToken(Integer userId);

    AuthUser verifyExpiration(String token);

    void deleteRefreshToken(Integer userId);
}
