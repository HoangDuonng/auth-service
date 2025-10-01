package com.graduationproject.auth_service.listener;

import com.graduationproject.auth_service.event.UserCreatedEventForAuthService;
import com.graduationproject.auth_service.service.AuthService;
import com.graduationproject.auth_service.dto.request.SaveAuthInfoRequestDTO;
import com.graduationproject.auth_service.repository.AuthUserRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class UserCreatedListener {
    private static final Logger logger = LoggerFactory.getLogger(UserCreatedListener.class);
    private final AuthService authService;
    private final AuthUserRepository authUserRepository;

    @RabbitListener(queues = "${rabbitmq.queue.user-created-auth}")
    @Transactional
    public void handleUserCreated(UserCreatedEventForAuthService event) {
        try {
            logger.info("Received user created event: {}", event);
            logger.debug("Event details - userId: {}, username: {}, email: {}, password length: {}",
                    event.getUserId(),
                    event.getUsername(),
                    event.getEmail(),
                    event.getPassword() != null ? event.getPassword().length() : 0);

            // Check if user already exists
            if (authUserRepository.existsByUserId(Integer.parseInt(event.getUserId()))) {
                logger.info("Auth info already exists for user: {}", event.getUsername());
                return;
            }

            SaveAuthInfoRequestDTO request = new SaveAuthInfoRequestDTO();
            request.setUserId(Integer.parseInt(event.getUserId()));
            request.setPassword(event.getPassword());
            request.setProvider("local");

            logger.debug("Saving auth info for user: {}", event.getUsername());
            authService.saveAuthInfo(request);
            logger.info("Successfully saved auth info for user: {}", event.getUsername());
        } catch (Exception e) {
            logger.error("Failed to save auth info for user: {}. Error: {}", event.getUsername(), e.getMessage(), e);
            // Không throw exception để tránh message bị đưa trở lại queue
        }
    }
}
