package com.graduationproject.auth_service.listener;

import com.graduationproject.auth_service.config.RabbitMQConfig;
import com.graduationproject.auth_service.event.UserCreatedEvent;
import com.graduationproject.auth_service.service.AuthService;
import com.graduationproject.auth_service.dto.request.SaveAuthInfoRequestDTO;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserCreatedListener {
    private static final Logger logger = LoggerFactory.getLogger(UserCreatedListener.class);
    private final AuthService authService;

    @RabbitListener(queues = "${rabbitmq.queue.user-created}")
    public void handleUserCreated(UserCreatedEvent event) {
        logger.info("Received user created event: {}", event);

        SaveAuthInfoRequestDTO request = new SaveAuthInfoRequestDTO();
        request.setUserId(Integer.parseInt(event.getUserId()));
        request.setPassword(event.getPassword());
        request.setProvider("local");

        try {
            authService.saveAuthInfo(request);
            logger.info("Successfully saved auth info for user: {}", event.getUsername());
        } catch (Exception e) {
            logger.error("Failed to save auth info for user: {}", event.getUsername(), e);
            throw new RuntimeException("Failed to save auth info", e);
        }
    }
}
