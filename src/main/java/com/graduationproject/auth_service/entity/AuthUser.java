package com.graduationproject.auth_service.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "auth_users")
public class AuthUser extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(columnDefinition = "SERIAL")
    private Integer id;

    @Column(name = "user_id", nullable = false, unique = true, columnDefinition = "INTEGER")
    private Integer userId;

    @Column(nullable = false)
    private String password;

    @Column(name = "refresh_token", columnDefinition = "TEXT")
    private String token;

    @Column(name = "token_expiry")
    private LocalDateTime tokenExpiry;

    @Column(name = "email_verification_token", columnDefinition = "TEXT")
    private String emailVerificationToken;

    @Column(name = "email_verification_token_expired_at")
    private LocalDateTime emailVerificationTokenExpiredAt;

    @Column(name = "reset_password_token", columnDefinition = "TEXT")
    private String resetPasswordToken;

    @Column(name = "reset_password_token_expired_at")
    private LocalDateTime resetPasswordTokenExpiredAt;

    @Column(nullable = false, columnDefinition = "VARCHAR(50)")
    private String provider;

    @Column(name = "provider_id", columnDefinition = "VARCHAR(100)")
    private String providerId;
}
