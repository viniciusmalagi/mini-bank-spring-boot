package com.vmlg.bank.bank.dtos;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;


public record TransactionProcessDTO(
    UUID sender, UUID receiver, BigDecimal value,
    String transactionStatus
) 
{
}
