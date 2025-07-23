package com.amex.AccountService.dto;

import lombok.*;
import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AccountRequestDTO {
    private Long userId;
    private BigDecimal balance;
    private String currency;
}