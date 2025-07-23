package com.amex.TransactionService.service;


import com.amex.TransactionService.dto.TransactionRequestDTO;
import com.amex.TransactionService.dto.TransactionResponseDTO;
import com.amex.TransactionService.dto.TransactionUserDetailsResponseDto;
import com.amex.TransactionService.dto.UserResponseDto;
import com.amex.TransactionService.repository.TransactionRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureMockMvc
class TransactionServiceTest {


    @Autowired
    private TransactionService service;

    @Test
    void testTransactionCreation() {
        TransactionRequestDTO dto = TransactionRequestDTO.builder()
                .accountId(1L)
                .amount(BigDecimal.valueOf(2000))
                .type("CREDIT")
                .description("Test deposit")
                .build();

        // Act
        TransactionResponseDTO result = service.createTransaction(dto);

        assertNotNull(result.getId());
    }

    @Test
    void testGetTransactionsByAccountAndDateRange() {
        Long accountId = 1L;
        LocalDate from = LocalDate.of(2025, 7, 1);
        LocalDate to = LocalDate.of(2025, 7, 31);

        TransactionUserDetailsResponseDto result = service.getTransactionsByAccountAndDateRange(accountId, from, to);

        assertNotNull(result.getUserData());
        assertNotNull(result.getTransactionList());
        assertFalse(result.getTransactionList().isEmpty());
    }
}