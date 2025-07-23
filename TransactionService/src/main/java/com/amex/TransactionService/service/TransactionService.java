package com.amex.TransactionService.service;

import com.amex.TransactionService.dto.TransactionRequestDTO;
import com.amex.TransactionService.dto.TransactionResponseDTO;
import com.amex.TransactionService.exception.ResourceNotFoundException;
import com.amex.TransactionService.model.Transaction;
import com.amex.TransactionService.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TransactionService {

    @Autowired
    private TransactionRepository repository;

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
}