package com.amex.TransactionService.controller;

import com.amex.TransactionService.dto.TransactionRequestDTO;
import com.amex.TransactionService.dto.TransactionResponseDTO;
import com.amex.TransactionService.dto.TransactionUserDetailsResponseDto;
import com.amex.TransactionService.dto.UserResponseDto;
import com.amex.TransactionService.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
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

    @GetMapping("/by-account")
    public TransactionUserDetailsResponseDto getTransactionsByAccountAndDateRange(
            @RequestParam Long accountId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to
    ) {
        return service.getTransactionsByAccountAndDateRange(accountId, from, to);
    }
}

