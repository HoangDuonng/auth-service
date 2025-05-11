package com.graduationproject.auth_service.repository;

import com.graduationproject.auth_service.entity.AuthUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AuthUserRepository extends JpaRepository<AuthUser, Integer> {
    Optional<AuthUser> findByUserId(Integer userId);

    Optional<AuthUser> findByToken(String token);

    Optional<AuthUser> findByEmailVerificationToken(String token);

    Optional<AuthUser> findByResetPasswordToken(String token);
}
