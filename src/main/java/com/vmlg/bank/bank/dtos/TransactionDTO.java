package com.vmlg.bank.bank.dtos;

import java.math.BigDecimal;
import java.util.UUID;

public record TransactionDTO(
    BigDecimal value, UUID senderId, UUID receiverId
) {}
