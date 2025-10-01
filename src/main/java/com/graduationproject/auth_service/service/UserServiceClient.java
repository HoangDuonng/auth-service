package com.graduationproject.auth_service.service;

import com.graduationproject.auth_service.dto.response.UserResponseDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "user-service", url = "${user-service.url}")
public interface UserServiceClient {
    @GetMapping("/users/{id}")
    UserResponseDTO getUserById(@PathVariable Integer id);

    @GetMapping("/users/username/{username}")
    UserResponseDTO getUserByUsername(@PathVariable String username);
}
