package com.amex.AccountService.service;

import com.amex.AccountService.dto.AccountRequestDTO;
import com.amex.AccountService.dto.AccountResponseDTO;
import com.amex.AccountService.exception.ResourceNotFoundException;
import com.amex.AccountService.model.Account;
import com.amex.AccountService.repository.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
public class AccountService {

    @Autowired
    private AccountRepository repository;

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    private static final String USER_VALIDATION_REQUEST = "user-validation-request";
    private static final ConcurrentHashMap<Long, CompletableFuture<Boolean>> userValidationMap = new ConcurrentHashMap<>();

    public AccountResponseDTO createAccount(AccountRequestDTO dto) {
        boolean isValidUser = validateUserIdViaKafka(dto.getUserId());
        if (!isValidUser) {
            throw new ResourceNotFoundException("User with ID " + dto.getUserId() + " not found");
        }

        Account account = Account.builder()
                .userId(dto.getUserId())
                .balance(dto.getBalance())
                .currency(dto.getCurrency())
                .build();

        return toResponseDTO(repository.save(account));
    }

    private boolean validateUserIdViaKafka(Long userId) {
        CompletableFuture<Boolean> future = new CompletableFuture<>();
        userValidationMap.put(userId, future);
        kafkaTemplate.send(USER_VALIDATION_REQUEST, userId.toString());
        try {
            return future.get(3, TimeUnit.SECONDS);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            userValidationMap.remove(userId);
        }
    }

    public static void respondToUserValidation(Long userId, boolean exists) {
        CompletableFuture<Boolean> future = userValidationMap.get(userId);
        if (future != null) {
            future.complete(exists);
        }
    }

    public AccountResponseDTO getAccount(Long id) {
        Account account = repository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new ResourceNotFoundException("Account not found with ID: " + id));
        return toResponseDTO(account);
    }

    public List<AccountResponseDTO> getAllAccounts() {
        return repository.findAllByDeletedFalse()
                .stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    public AccountResponseDTO updateAccount(Long id, AccountRequestDTO dto) {
        Account account = repository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new ResourceNotFoundException("Account not found with ID: " + id));

        account.setBalance(dto.getBalance());
        account.setCurrency(dto.getCurrency());

        return toResponseDTO(repository.save(account));
    }

    public void softDeleteAccount(Long id) {
        Account account = repository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new ResourceNotFoundException("Account not found with ID: " + id));
        account.setDeleted(true);
        repository.save(account);
    }

    private AccountResponseDTO toResponseDTO(Account account) {
        return AccountResponseDTO.builder()
                .id(account.getId())
                .userId(account.getUserId())
                .balance(account.getBalance())
                .currency(account.getCurrency())
                .deleted(account.isDeleted())
                .build();
    }
}