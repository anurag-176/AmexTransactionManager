package com.amex.TransactionService.dto;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TransactionUserDetailsResponseDto {

    private List<TransactionResponseDTO> transactionList;

    private UserResponseDto userData;
}
