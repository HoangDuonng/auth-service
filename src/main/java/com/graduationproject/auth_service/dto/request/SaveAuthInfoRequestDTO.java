package com.graduationproject.auth_service.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class SaveAuthInfoRequestDTO {
    @NotNull(message = "User ID is required")
    private Integer userId;

    @NotBlank(message = "Password is required")
    private String password;

    @NotBlank(message = "Provider is required")
    private String provider;
}
