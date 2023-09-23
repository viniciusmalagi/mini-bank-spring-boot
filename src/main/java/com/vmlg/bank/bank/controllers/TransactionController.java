package com.vmlg.bank.bank.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.vmlg.bank.bank.domain.transaction.Transaction;
import com.vmlg.bank.bank.dtos.TransactionDTO;
import com.vmlg.bank.bank.dtos.TransactionSuccessfullyDTO;
import com.vmlg.bank.bank.exceptions.TransactionsException;
import com.vmlg.bank.bank.services.TransactionService;

@RestController
@RequestMapping("/transactions")
public class TransactionController {
    @Autowired
    private TransactionService transactionService;

    @PostMapping
    public ResponseEntity createTransaction(@RequestBody TransactionDTO transaction) throws TransactionsException{
        Transaction newTransaction = transactionService.createTransaction(transaction);
        return ResponseEntity.ok(
            new TransactionSuccessfullyDTO(
                newTransaction.getSender().getId(),
                newTransaction.getReceiver().getId(),
                newTransaction.getAmount(),
                newTransaction.getTimestamp()
            ));
    }

    @GetMapping
    public ResponseEntity<List<Transaction>> getAllTransactions(){
        List<Transaction> transactions = transactionService.getAllTransactions();
        return new ResponseEntity<List<Transaction>>(transactions, HttpStatus.OK);
    }
}
