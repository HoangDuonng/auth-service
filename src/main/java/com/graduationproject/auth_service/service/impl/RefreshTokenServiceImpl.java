package com.graduationproject.auth_service.service.impl;

import com.graduationproject.auth_service.entity.AuthUser;
import com.graduationproject.auth_service.exception.TokenExpiredException;
import com.graduationproject.auth_service.repository.AuthUserRepository;
import com.graduationproject.auth_service.service.RefreshTokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RefreshTokenServiceImpl implements RefreshTokenService {
    private final AuthUserRepository authUserRepository;

    @Value("${jwt.refresh-token.expiration}")
    private Long refreshTokenExpiration;

    @Override
    @Transactional
    public AuthUser createRefreshToken(Integer userId) {
        AuthUser authUser = authUserRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        String token = UUID.randomUUID().toString();
        LocalDateTime expiryDate = LocalDateTime.now(ZoneOffset.UTC)
                .plusSeconds(refreshTokenExpiration / 1000);

        authUser.setToken(token);
        authUser.setTokenExpiry(expiryDate);

        return authUserRepository.save(authUser);
    }

    @Override
    @Transactional
    public AuthUser verifyExpiration(String token) {
        AuthUser authUser = authUserRepository.findByToken(token)
                .orElseThrow(() -> new RuntimeException("Refresh token not found"));

        if (authUser.getTokenExpiry().isBefore(LocalDateTime.now(ZoneOffset.UTC))) {
            authUserRepository.delete(authUser);
            throw new RuntimeException("Refresh token was expired");
        }

        return authUser;
    }

    @Override
    @Transactional
    public void deleteRefreshToken(Integer userId) {
        AuthUser authUser = authUserRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Auth user not found"));

        authUser.setToken(null);
        authUser.setTokenExpiry(null);
        authUserRepository.save(authUser);
    }
}
