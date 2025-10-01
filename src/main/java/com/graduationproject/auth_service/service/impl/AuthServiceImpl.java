package com.graduationproject.auth_service.service.impl;

import com.graduationproject.common.dto.LoginMessageDTO;
import com.graduationproject.common.dto.LoginResponseMessageDTO;
import com.graduationproject.auth_service.dto.request.LoginRequestDTO;
import com.graduationproject.auth_service.dto.request.RefreshTokenRequestDTO;
import com.graduationproject.auth_service.dto.request.SaveAuthInfoRequestDTO;
import com.graduationproject.auth_service.dto.response.TokenResponseDTO;
import com.graduationproject.common.dto.UserResponseDTO;
import com.graduationproject.auth_service.entity.AuthUser;
import com.graduationproject.auth_service.repository.AuthUserRepository;
import com.graduationproject.auth_service.service.AuthService;
import com.graduationproject.auth_service.service.JwtService;
import com.graduationproject.auth_service.service.RefreshTokenService;
import com.graduationproject.auth_service.service.AuthorizationServiceClient;
import com.graduationproject.auth_service.exception.UserNotFoundException;
import com.graduationproject.auth_service.exception.InvalidPasswordException;
import com.graduationproject.auth_service.exception.AuthenticationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    private final JwtService jwtService;
    private final RefreshTokenService refreshTokenService;
    private final AuthUserRepository authUserRepository;
    private final PasswordEncoder passwordEncoder;
    private final RabbitTemplate rabbitTemplate;
    private final AuthorizationServiceClient authorizationServiceClient;

    @Value("${rabbitmq.exchange.auth-events}")
    private String exchangeAuthEvents;

    @Value("${rabbitmq.routing-key.auth-login}")
    private String routingKeyAuthLogin;

    @Value("${rabbitmq.routing-key.auth-login-response}")
    private String routingKeyAuthLoginResponse;

    @Value("${rabbitmq.login-timeout:5000}")
    private long loginTimeout;

    @Override
    public TokenResponseDTO login(LoginRequestDTO request) {
        log.debug("Processing login request for email: {}", request.getEmail());
        try {
            // 1. Gửi sang user-service để lấy user theo email
            String correlationId = UUID.randomUUID().toString();
            LoginMessageDTO loginMessage = LoginMessageDTO.builder()
                    .email(request.getEmail())
                    .correlationId(correlationId)
                    .build();

            rabbitTemplate.convertAndSend(exchangeAuthEvents, routingKeyAuthLogin, loginMessage);

            LoginResponseMessageDTO response = null;
            long start = System.currentTimeMillis();
            while (System.currentTimeMillis() - start < loginTimeout) {
                LoginResponseMessageDTO msg = (LoginResponseMessageDTO) rabbitTemplate
                        .receiveAndConvert("auth-login-response-queue");
                if (msg != null && correlationId.equals(msg.getCorrelationId())) {
                    response = msg;
                    break;
                }
                Thread.sleep(100);
            }

            if (response == null || !response.isSuccess()) {
                log.warn("User not found for email: {}", request.getEmail());
                throw new UserNotFoundException("Email không tồn tại trong hệ thống");
            }

            // 2. Dùng userId để kiểm tra password
            AuthUser authUser = authUserRepository.findByUserId(response.getUserId())
                    .orElseThrow(() -> new AuthenticationException("Tài khoản chưa được thiết lập mật khẩu"));

            if (!passwordEncoder.matches(request.getPassword(), authUser.getPassword())) {
                log.warn("Invalid password for userId: {}", response.getUserId());
                throw new InvalidPasswordException("Mật khẩu không chính xác");
            }
            // 3. Lấy roles từ authorization-service
            var roles = authorizationServiceClient.getUserRoles(response.getUserId().longValue());
            // 4. Tạo token
            UserResponseDTO userResponse = UserResponseDTO.builder()
                    .id(response.getUserId().longValue())
                    .email(response.getEmail())
                    .username(response.getUsername())
                    .first_name(response.getFirst_name())
                    .last_name(response.getLast_name())
                    .fullName(response.getFullName())
                    .gender(response.getGender())
                    .avatar(response.getAvatar())
                    .cover(response.getCover())
                    .dob(response.getDob())
                    .phone(response.getPhone())
                    .address(response.getAddress())
                    .isActivated(response.getIsActivated())
                    .isDeleted(response.getIsDeleted())
                    .createdAt(response.getCreatedAt())
                    .updatedAt(response.getUpdatedAt())
                    .build();
            var accessToken = jwtService.generateToken(userResponse, roles);
            var refreshToken = refreshTokenService.createRefreshToken(response.getUserId());

            log.info("Login successful for user: {}", response.getEmail());

            return TokenResponseDTO.builder()
                    .accessToken(accessToken)
                    .refreshToken(refreshToken.getToken())
                    .tokenType("Bearer")
                    .expiresIn(jwtService.getExpirationTime())
                    .user(userResponse)
                    .build();
        } catch (IllegalArgumentException e) {
            log.warn("Login failed for email {}: {}", request.getEmail(), e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Error processing login request for email: {}", request.getEmail(), e);
            throw new RuntimeException("Internal server error");
        }
    }

    @Override
    public TokenResponseDTO refreshToken(RefreshTokenRequestDTO request) {
        log.debug("Processing refresh token request");
        try {
            var refreshToken = refreshTokenService.verifyExpiration(request.getRefreshToken());

            // Create UserResponseDTO for token generation
            UserResponseDTO userResponse = UserResponseDTO.builder()
                    .id(refreshToken.getUserId().longValue())
                    .build();

            // Lấy roles của user
            var roles = authorizationServiceClient.getUserRoles(refreshToken.getUserId().longValue());

            var accessToken = jwtService.generateToken(userResponse, roles);

            log.info("Token refresh successful for user ID: {}", refreshToken.getUserId());

            return TokenResponseDTO.builder()
                    .accessToken(accessToken)
                    .refreshToken(refreshToken.getToken())
                    .tokenType("Bearer")
                    .expiresIn(jwtService.getExpirationTime())
                    .build();
        } catch (Exception e) {
            log.error("Error refreshing token", e);
            throw new RuntimeException("Token refresh failed: " + e.getMessage());
        }
    }

    @Override
    @Transactional
    public void saveAuthInfo(SaveAuthInfoRequestDTO request) {
        log.debug("Saving auth info for user ID: {}", request.getUserId());
        try {
            AuthUser authUser = AuthUser.builder()
                    .userId(request.getUserId())
                    .password(passwordEncoder.encode(request.getPassword()))
                    .provider(request.getProvider())
                    .build();

            authUserRepository.save(authUser);
            log.info("Auth info saved successfully for user ID: {}", request.getUserId());
        } catch (Exception e) {
            log.error("Error saving auth info for user ID: {}", request.getUserId(), e);
            throw new RuntimeException("Failed to save auth info: " + e.getMessage());
        }
    }
}
