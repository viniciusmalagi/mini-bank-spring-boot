package com.vmlg.bank.bank.repositores;

import org.springframework.data.jpa.repository.JpaRepository;
import com.vmlg.bank.bank.domain.transaction.Transaction;

public interface TransactionRepository extends JpaRepository<Transaction,Long>{
    
}
