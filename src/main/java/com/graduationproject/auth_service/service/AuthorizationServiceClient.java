package com.graduationproject.auth_service.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class AuthorizationServiceClient {
    @Value("${authorization-service.url}")
    private String authorizationServiceUrl;

    private final RestTemplate restTemplate = new RestTemplate();

    public List<String> getUserRoles(Long userId) {
        String url = authorizationServiceUrl + "/api/internal/users/" + userId + "/roles";

        // Gọi API internal không cần authentication
        List<Map<String, Object>> response = restTemplate.getForObject(url, List.class);
        if (response == null)
            return List.of();
        return response.stream()
                .map(role -> (String) role.get("name"))
                .collect(Collectors.toList());
    }
}
