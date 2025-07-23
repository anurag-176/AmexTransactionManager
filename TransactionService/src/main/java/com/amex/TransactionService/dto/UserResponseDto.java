package com.amex.TransactionService.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserResponseDto {
    private Long id;

    private String name;

    private String email;

    private boolean deleted = false;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

}
