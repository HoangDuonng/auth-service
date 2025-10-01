package com.graduationproject.auth_service.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserCreatedEventForAuthService {
    private String userId;
    private String username;
    private String email;
    private String password;
}
