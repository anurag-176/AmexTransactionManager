package com.amex.AccountService.kafka;

import com.amex.AccountService.service.AccountService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class UserValidationListener {

    @KafkaListener(topics = "user-validation-response", groupId = "account-service")
    public void listenUserValidationResponse(String message) {
        try {
            String[] parts = message.split(":");
            Long userId = Long.parseLong(parts[0]);
            boolean exists = Boolean.parseBoolean(parts[1]);
            AccountService.respondToUserValidation(userId, exists);
        } catch (Exception ignored) {
        }
    }
}
