package com.vmlg.bank.bank.controllers;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.vmlg.bank.bank.domain.transaction.Transaction;
import com.vmlg.bank.bank.domain.transaction.TransactionStatus;
import com.vmlg.bank.bank.dtos.TransactionDTO;
import com.vmlg.bank.bank.dtos.TransactionProcessDTO;
import com.vmlg.bank.bank.dtos.TransactionSuccessfullyDTO;
import com.vmlg.bank.bank.exceptions.TransactionsException;
import com.vmlg.bank.bank.services.transaction.TransactionService;

@RestController
@RequestMapping("/transactions")
public class TransactionController {
    @Autowired
    private TransactionService transactionService;

    @PostMapping
    public ResponseEntity<TransactionProcessDTO> createTransaction(@RequestBody TransactionDTO transaction) throws TransactionsException{
        transactionService.processTransaction(transaction);
        return ResponseEntity.ok(
            new TransactionProcessDTO(
                transaction.senderId(),
                transaction.receiverId(),
                transaction.value(),
                TransactionStatus.PROCESSING.name()
            ));
    }

    @GetMapping
    public ResponseEntity<List<Transaction>> getAllTransactions(){
        List<Transaction> transactions = transactionService.getAllTransactions();
        return new ResponseEntity<List<Transaction>>(transactions, HttpStatus.OK);
    }

    @GetMapping("{id}")
    public ResponseEntity<TransactionSuccessfullyDTO> getTransaction(
        @PathVariable("id") UUID uuid) throws TransactionsException{
        Transaction transaction = transactionService.getTransactionById(uuid);
        return ResponseEntity.ok(
            new TransactionSuccessfullyDTO(
                transaction.getSender().getId(),
                transaction.getReceiver().getId(),
                transaction.getAmount(),
                transaction.getTimestamp(),
                TransactionStatus.SUCCESS.name()
            ));
    }
}
