package com.amex.AccountService.dto;

import lombok.*;
import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AccountResponseDTO {
    private Long id;
    private Long userId;
    private BigDecimal balance;
    private String currency;
    private boolean deleted;
}
