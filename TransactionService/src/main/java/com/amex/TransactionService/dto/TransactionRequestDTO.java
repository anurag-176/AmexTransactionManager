package com.amex.TransactionService.dto;

import lombok.*;
import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TransactionRequestDTO {
    private Long accountId;
    private BigDecimal amount;
    private String type;
    private String description;
}