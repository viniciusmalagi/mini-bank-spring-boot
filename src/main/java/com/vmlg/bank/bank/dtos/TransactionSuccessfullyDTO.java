package com.vmlg.bank.bank.dtos;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record TransactionSuccessfullyDTO(
    UUID sender, UUID receiver, BigDecimal value, LocalDateTime timestamp
) {
    
}
