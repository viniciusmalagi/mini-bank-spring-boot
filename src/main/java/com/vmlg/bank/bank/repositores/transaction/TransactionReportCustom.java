package com.vmlg.bank.bank.repositores.transaction;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.vmlg.bank.bank.domain.user.User;


public interface TransactionReportCustom {

    UUID getId();
    BigDecimal getAmount();
    LocalDateTime getTimestamp();

    default UUID getReceiverId(){
        return getReceiver().getId();
    }

    default String getReceiverFullName(){
        return getReceiver().getFirstName().concat(" ")
        .concat(getReceiver().getLastName());
    }

    @JsonIgnore
    User getSender();

    @JsonIgnore
    User getReceiver();
}

