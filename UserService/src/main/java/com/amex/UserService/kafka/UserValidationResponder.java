package com.amex.UserService.kafka;

import com.amex.UserService.model.User;
import com.amex.UserService.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import java.util.Optional;

@Slf4j
@Component
public class UserValidationResponder {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    @KafkaListener(topics = "user-validation-request", groupId = "user-service")
    public void listenUserValidationRequest(String userIdStr) {
        try {
            if (userIdStr == null || userIdStr.trim().isEmpty()) {
                throw new IllegalArgumentException("Received empty userId string from Kafka");
            }

            String cleaned = userIdStr.replaceAll("[^\\d]", "");

            if (cleaned.isEmpty()) {
                throw new IllegalArgumentException("Parsed userId string is empty after cleaning: " + userIdStr);
            }

            Long userId = Long.parseLong(cleaned);
            boolean exists = userRepository.findByIdAndDeletedFalse(userId).isPresent();
            kafkaTemplate.send("user-validation-response", cleaned + ":" + exists);
        } catch (Exception e) {
            System.err.println("Failed to parse or handle userId: '" + userIdStr + "'");
            e.printStackTrace();
        }
    }

    @KafkaListener(topicPattern = "user-data-request", groupId = "user-service")
    public void listenUserLookupByAccountId(String message) {
        try {
            log.info("USR");
            String[] parts = message.split(":");
            if (parts.length != 2) return;
            String replyTo = parts[0];
            Long userId = Long.parseLong(parts[1]);

            Optional<User> userOpt = userRepository.findByIdAndDeletedFalse(userId);

            if (userOpt.isPresent()) {
                User user = userOpt.get();
                String response = objectMapper.writeValueAsString(user);
                kafkaTemplate.send(replyTo, response);
            } else {
                kafkaTemplate.send(replyTo, "User not found for userId: " + userId);
            }
        } catch (Exception e) {
            System.err.println("Failed to handle user lookup message: '" + message + "'");
            e.printStackTrace();
        }
    }
}