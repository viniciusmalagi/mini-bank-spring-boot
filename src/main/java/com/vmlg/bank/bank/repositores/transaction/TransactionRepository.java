package com.vmlg.bank.bank.repositores.transaction;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import com.vmlg.bank.bank.domain.transaction.Transaction;
import com.vmlg.bank.bank.domain.user.User;


public interface TransactionRepository extends JpaRepository<Transaction, UUID>{
    Optional<List<TransactionReportCustom>> findAllBySender(User user);
}
