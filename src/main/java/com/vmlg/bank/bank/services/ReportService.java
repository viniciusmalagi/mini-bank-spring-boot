package com.vmlg.bank.bank.services;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.vmlg.bank.bank.domain.user.User;
import com.vmlg.bank.bank.exceptions.UsersException;
import com.vmlg.bank.bank.repositores.transaction.TransactionReportCustom;
import com.vmlg.bank.bank.repositores.transaction.TransactionRepository;

@Service
public class ReportService extends UserService{
    
    @Autowired
    TransactionRepository transactionRepository;

    public List<TransactionReportCustom> getAllTransactionsBySenderId(UUID senderID) throws UsersException{
        User sender = userRepository.findById(senderID).orElseThrow(() -> new UsersException("User not found!"));
        return transactionRepository.findAllBySender(sender).orElseThrow(() -> new UsersException("There're no transactions!"));
    }
}
