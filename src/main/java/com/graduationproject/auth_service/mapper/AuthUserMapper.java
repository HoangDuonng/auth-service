package com.graduationproject.auth_service.mapper;

import com.graduationproject.auth_service.dto.request.SaveAuthInfoRequestDTO;
import com.graduationproject.auth_service.entity.AuthUser;
import org.springframework.stereotype.Component;

@Component
public class AuthUserMapper implements BaseMapper<AuthUser, SaveAuthInfoRequestDTO> {

    @Override
    public SaveAuthInfoRequestDTO toDto(AuthUser authUser) {
        if (authUser == null) {
            return null;
        }

        SaveAuthInfoRequestDTO dto = new SaveAuthInfoRequestDTO();
        dto.setUserId(authUser.getUserId());
        dto.setPassword(authUser.getPassword());
        dto.setProvider(authUser.getProvider());
        return dto;
    }

    public AuthUser toEntity(SaveAuthInfoRequestDTO request) {
        if (request == null) {
            return null;
        }

        return AuthUser.builder()
                .userId(request.getUserId())
                .password(request.getPassword())
                .provider(request.getProvider())
                .build();
    }
}
