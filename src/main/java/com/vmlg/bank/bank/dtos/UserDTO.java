package com.vmlg.bank.bank.dtos;

import java.math.BigDecimal;

import com.vmlg.bank.bank.domain.user.UserType;


public record UserDTO(
    String firstName, String lastName, String document,
    BigDecimal balance, String email, String password, UserType userType) {
}
