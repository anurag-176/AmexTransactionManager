package com.amex.AccountService.service;

import com.amex.AccountService.dto.AccountRequestDTO;
import com.amex.AccountService.dto.AccountResponseDTO;
import com.amex.AccountService.model.Account;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@AutoConfigureMockMvc
public class AccountServiceTest {

    @Autowired
    private AccountService service;

    @Test
    void addAccount() {
        AccountRequestDTO dto = AccountRequestDTO.builder()
                .userId(1L)
                .balance(BigDecimal.valueOf(5000))
                .currency("INR")
                .build();

        // Act
        AccountResponseDTO result = service.createAccount(dto);

        // Assert
        assertNotNull(result.getId());
    }
}
