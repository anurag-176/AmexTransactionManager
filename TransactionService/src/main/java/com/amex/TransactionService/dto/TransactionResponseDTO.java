package com.amex.TransactionService.dto;

import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TransactionResponseDTO {
    private Long id;
    private Long accountId;
    private BigDecimal amount;
    private String type;
    private String description;
    private LocalDateTime createdAt;
}