package com.amex.TransactionService.service;

import com.amex.TransactionService.dto.TransactionRequestDTO;
import com.amex.TransactionService.dto.TransactionResponseDTO;
import com.amex.TransactionService.dto.UserResponseDto;
import com.amex.TransactionService.exception.ResourceNotFoundException;
import com.amex.TransactionService.kafka.UserDataFetcher;
import com.amex.TransactionService.model.Transaction;
import com.amex.TransactionService.repository.TransactionRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@Service
public class TransactionService {

    @Autowired
    private TransactionRepository repository;

    @Autowired
    private UserDataFetcher userDataFetcher;

    @Autowired
    private ObjectMapper objectMapper;

    public TransactionResponseDTO createTransaction(TransactionRequestDTO dto) {
        Transaction transaction = Transaction.builder()
                .accountId(dto.getAccountId())
                .amount(dto.getAmount())
                .type(dto.getType())
                .description(dto.getDescription())
                .build();
        return toResponseDTO(repository.save(transaction));
    }

    public TransactionResponseDTO getTransaction(Long id) {
        Transaction transaction = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Transaction not found with ID: " + id));
        return toResponseDTO(transaction);
    }

    public List<TransactionResponseDTO> getTransactionsByAccountId(Long accountId) {
        return repository.findAllByAccountId(accountId)
                .stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    private TransactionResponseDTO toResponseDTO(Transaction transaction) {
        return TransactionResponseDTO.builder()
                .id(transaction.getId())
                .accountId(transaction.getAccountId())
                .amount(transaction.getAmount())
                .type(transaction.getType())
                .description(transaction.getDescription())
                .createdAt(transaction.getCreatedAt())
                .build();
    }

    public UserResponseDto fetchUserByAccountId(String accountId) throws Exception {
        String userObject = userDataFetcher.fetchUserByAccountId(accountId);
        if (Objects.isNull(userObject)) {
            log.info("User not found");
            throw new ResourceNotFoundException("User not found");
        } else {
            return objectMapper.readValue(userObject, UserResponseDto.class);
        }
    }
}