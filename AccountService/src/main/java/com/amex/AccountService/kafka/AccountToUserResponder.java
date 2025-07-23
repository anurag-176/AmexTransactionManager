package com.amex.AccountService.kafka;

import com.amex.AccountService.model.Account;
import com.amex.AccountService.repository.AccountRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import java.util.Optional;

@Slf4j
@Component
public class AccountToUserResponder {

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    @KafkaListener(topics = "account-to-user-request", groupId = "account-service")
    public void handleAccountToUserRequest(String message) {
        try {
            log.info("ACC");
            String[] parts = message.split(":");
            if (parts.length != 2) return;

            String replyTopic = parts[0];
            Long accountId = Long.parseLong(parts[1]);

            Optional<Account> accountOpt = accountRepository.findByIdAndDeletedFalse(accountId);
            if (accountOpt.isPresent()) {
                Long userId = accountOpt.get().getUserId();
                String payload = replyTopic + ":" + userId;
                kafkaTemplate.send("user-data-request", payload);
                log.info("REQ SENT");
            } else {
                kafkaTemplate.send(replyTopic, "Account not found for accountId: " + accountId);
                log.info("ERR SENT to topic:{}", replyTopic);
            }
        } catch (Exception e) {
            System.err.println("Error resolving user from account: " + message);
            e.printStackTrace();
        }
    }
}
