package com.graduationproject.auth_service.service.impl;

import com.graduationproject.auth_service.dto.request.LoginRequestDTO;
import com.graduationproject.auth_service.dto.request.RefreshTokenRequestDTO;
import com.graduationproject.auth_service.dto.request.SaveAuthInfoRequestDTO;
import com.graduationproject.auth_service.dto.response.TokenResponseDTO;
import com.graduationproject.auth_service.entity.AuthUser;
import com.graduationproject.auth_service.repository.AuthUserRepository;
import com.graduationproject.auth_service.service.AuthService;
import com.graduationproject.auth_service.service.JwtService;
import com.graduationproject.auth_service.service.RefreshTokenService;
import com.graduationproject.auth_service.service.UserServiceClient;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final RefreshTokenService refreshTokenService;
    private final UserServiceClient userServiceClient;
    private final AuthUserRepository authUserRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public TokenResponseDTO login(LoginRequestDTO request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));

        var user = userServiceClient.getUserByUsername(request.getUsername());
        var accessToken = jwtService.generateToken(user);
        var refreshToken = refreshTokenService.createRefreshToken(user.getId());

        return TokenResponseDTO.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken.getToken())
                .tokenType("Bearer")
                .expiresIn(jwtService.getExpirationTime())
                .build();
    }

    @Override
    public TokenResponseDTO refreshToken(RefreshTokenRequestDTO request) {
        var refreshToken = refreshTokenService.verifyExpiration(request.getRefreshToken());
        var user = userServiceClient.getUserById(refreshToken.getUserId());
        var accessToken = jwtService.generateToken(user);

        return TokenResponseDTO.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken.getToken())
                .tokenType("Bearer")
                .expiresIn(jwtService.getExpirationTime())
                .build();
    }

    @Override
    @Transactional
    public void saveAuthInfo(SaveAuthInfoRequestDTO request) {
        // Create auth user directly from the request
        AuthUser authUser = AuthUser.builder()
                .userId(request.getUserId())
                .password(passwordEncoder.encode(request.getPassword()))
                .provider(request.getProvider())
                .build();

        authUserRepository.save(authUser);
    }
}
