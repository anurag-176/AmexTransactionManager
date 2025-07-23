package com.amex.TransactionService.controller;

import com.amex.TransactionService.dto.TransactionRequestDTO;
import com.amex.TransactionService.dto.TransactionResponseDTO;
import com.amex.TransactionService.dto.UserResponseDto;
import com.amex.TransactionService.kafka.UserDataFetcher;
import com.amex.TransactionService.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/transactions")
public class TransactionController {

    @Autowired
    private TransactionService service;

    @PostMapping
    public TransactionResponseDTO create(@RequestBody TransactionRequestDTO dto) {
        return service.createTransaction(dto);
    }

    @GetMapping("/{id}")
    public TransactionResponseDTO get(@PathVariable Long id) {
        return service.getTransaction(id);
    }

    @GetMapping("/account/{accountId}")
    public List<TransactionResponseDTO> getByAccount(@PathVariable Long accountId) {
        return service.getTransactionsByAccountId(accountId);
    }

    @GetMapping("/user-by-account/{accountId}")
    public UserResponseDto getUserByAccountId(@PathVariable String accountId) throws Exception {
        return service.fetchUserByAccountId(accountId);
    }
}

