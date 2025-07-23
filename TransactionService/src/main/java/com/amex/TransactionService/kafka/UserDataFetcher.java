package com.amex.TransactionService.kafka;

import com.amex.TransactionService.exception.ResourceNotFoundException;
import com.amex.TransactionService.exception.TransactionException;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import java.util.UUID;
import java.util.concurrent.*;

@Slf4j
@Component
public class UserDataFetcher {

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    private final ConcurrentHashMap<String, CompletableFuture<String>> responseMap = new ConcurrentHashMap<>();

    public String fetchUserByAccountId(String accountId) throws TransactionException {
        String replyTopic = "user-data-response";
        String requestPayload = replyTopic + ":" + accountId;

        CompletableFuture<String> responseFuture = new CompletableFuture<>();
        responseMap.put(replyTopic, responseFuture);

        kafkaTemplate.send("account-to-user-request", requestPayload);

        try {
            return responseFuture.get(10, TimeUnit.SECONDS);
        } catch (Exception e) {
            throw new TransactionException(e.getMessage());
        }finally {
            responseMap.remove(replyTopic);
        }
    }

    @KafkaListener(topicPattern = "user-data-response", groupId = "transaction-service")
    public void handleUserResponse(String message, ConsumerRecord<String, String> record) {
        log.info("RESP");
        String topic = record.topic();
        CompletableFuture<String> future = responseMap.get(topic);
        if (future != null) {
            future.complete(message);
        }
    }
}
