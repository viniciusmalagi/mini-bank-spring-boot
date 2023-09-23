package com.vmlg.bank.bank.services;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.vmlg.bank.bank.domain.transaction.Transaction;
import com.vmlg.bank.bank.domain.user.User;
import com.vmlg.bank.bank.dtos.TransactionDTO;
import com.vmlg.bank.bank.exceptions.TransactionsException;
import com.vmlg.bank.bank.repositores.transaction.TransactionRepository;

@Service
public class TransactionService {
    @Autowired
    private UserService userService;

    @Autowired
    private TransactionRepository repository;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private NotificationService notificationService;

    public Transaction createTransaction(TransactionDTO transaction) throws TransactionsException{
        if (transaction.value().compareTo(new BigDecimal(0)) < 0){
            throw new TransactionsException("The transaction value must be positive.");
        }
        User sender = userService.findUserById(transaction.senderId());
        User receiver = userService.findUserById(transaction.receiverId());

        userService.validationTransaction(sender, transaction.value());
        boolean isAuthorized = authorizeTransaction(sender, transaction.value());
        if (!isAuthorized) {
           throw new TransactionsException("Transaction unauthorized"); 
        }
        Transaction bankTransaction = new Transaction();
        bankTransaction.setAmount(transaction.value());
        bankTransaction.setSender(sender);
        bankTransaction.setReceiver(receiver);
        bankTransaction.setTimestamp(LocalDateTime.now());
        sender.setBalance(sender.getBalance().subtract(transaction.value()));
        receiver.setBalance(receiver.getBalance().add(transaction.value()));
        repository.save(bankTransaction);
        userService.saveUser(sender);
        userService.saveUser(receiver);
        //notificationService.sendNotification(sender, "Transaction sended successfully");
        //notificationService.sendNotification(receiver, "Transaction received successfully");
        return bankTransaction;
    }

    public boolean authorizeTransaction(User sender, BigDecimal value){
        // TODO
        // ResponseEntity<Map> authorizationResponse = restTemplate.getForEntity("url", Map.class);
        // if (authorizationResponse.getStatusCode() == HttpStatus.OK) {
        //     String message = (String) authorizationResponse.getBody().get("message");
        //     return "Authorized".equalsIgnoreCase(message);
        // } else return false;
        return true;
    }
    public List<Transaction> getAllTransactions(){
        return repository.findAll();
    }

}