package com.amex.TransactionService.service;

import com.amex.TransactionService.dto.TransactionRequestDTO;
import com.amex.TransactionService.dto.TransactionResponseDTO;
import com.amex.TransactionService.dto.TransactionUserDetailsResponseDto;
import com.amex.TransactionService.dto.UserResponseDto;
import com.amex.TransactionService.exception.ResourceNotFoundException;
import com.amex.TransactionService.exception.TransactionException;
import com.amex.TransactionService.kafka.UserDataFetcher;
import com.amex.TransactionService.model.Transaction;
import com.amex.TransactionService.repository.TransactionRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
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

    public UserResponseDto fetchUserByAccountId(String accountId) throws TransactionException {
        String userObject = userDataFetcher.fetchUserByAccountId(accountId);
        if (Objects.isNull(userObject)) {
            log.info("User not found");
            throw new ResourceNotFoundException("User not found");
        } else {
            try {
                return objectMapper.readValue(userObject, UserResponseDto.class);
            }catch (Exception r){
                throw new TransactionException(r.getMessage());
            }
        }
    }

    public TransactionUserDetailsResponseDto getTransactionsByAccountAndDateRange(Long accountId, LocalDate from, LocalDate to) {

        UserResponseDto user = fetchUserByAccountId(accountId.toString());

        if (Objects.isNull(user)) {
            log.info("User not found");
            throw new ResourceNotFoundException("User with accountId="+accountId.toString()+" not found.");
        }

        LocalDateTime startOfDay = from.atStartOfDay();
        LocalDateTime endOfDay = to.atTime(23, 59, 59);
        List<Transaction> transactionList = repository.findByAccountIdAndCreatedAtBetween(accountId, startOfDay, endOfDay);

        List<TransactionResponseDTO> dtoList = transactionList.stream().map(t -> TransactionResponseDTO.builder()
                .type(t.getType())
                .createdAt(t.getCreatedAt())
                .amount(t.getAmount())
                .build()).collect(Collectors.toList());

        return TransactionUserDetailsResponseDto.builder()
                .transactionList(dtoList)
                .userData(user)
                .build();

    }
}