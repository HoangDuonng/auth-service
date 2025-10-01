package com.graduationproject.auth_service.service;

import com.graduationproject.common.dto.UserResponseDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "user-service", url = "${user-service.url}")
public interface UserServiceClient {
    @GetMapping("/users/{id}")
    UserResponseDTO getUserById(@PathVariable Integer id);

    @GetMapping("/users/username/{username}")
    UserResponseDTO getUserByUsername(@PathVariable String username);

    @GetMapping("/users/email/{email}")
    UserResponseDTO getUserByEmail(@PathVariable String email);
}
