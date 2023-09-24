package com.vmlg.bank.bank.services.transaction;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vmlg.bank.bank.domain.transaction.Transaction;
import com.vmlg.bank.bank.domain.user.User;
import com.vmlg.bank.bank.dtos.TransactionDTO;
import com.vmlg.bank.bank.exceptions.TransactionsException;
import com.vmlg.bank.bank.repositores.transaction.TransactionRepository;
import com.vmlg.bank.bank.services.NotificationService;
import com.vmlg.bank.bank.services.user.UserService;

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

    @Autowired
    private TransactionProducerService producerService;

    @Value("${api.message.transaction.topic}")
    private final String topic = "transaction";

    public void transactionValidation(TransactionDTO transaction) throws TransactionsException{
        User sender = userService.findUserById(transaction.senderId());
        if (transaction.value().compareTo(new BigDecimal(0)) < 0){
            throw new TransactionsException("The transaction value must be positive.");
        }
        userService.validationTransaction(sender, transaction.value());
        boolean isAuthorized = authorizeTransaction(sender, transaction.value());
        if (!isAuthorized) {
           throw new TransactionsException("Transaction unauthorized"); 
        }
    }

    public void processTransaction(TransactionDTO transaction){
        transactionValidation(transaction);
        producerService.sendMessage(transaction);
    }

    public Transaction createTransaction(TransactionDTO transaction) throws TransactionsException{
        User sender = userService.findUserById(transaction.senderId());
        User receiver = userService.findUserById(transaction.receiverId());
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
        // TODO send email
        //notificationService.sendNotification(sender, "Transaction sent successfully");
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

    public Transaction getTransactionById(UUID uuid) throws TransactionsException{
        return repository.getTransactionById(uuid).orElseThrow(() -> new TransactionsException("Transaction not found"));
    }

    @KafkaListener(topics = topic, groupId = "group-1")
    public void receiveMessage(String message) throws TransactionsException{
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            TransactionDTO transaction = objectMapper.readValue(message, TransactionDTO.class);
            createTransaction(transaction);
        } catch (JsonProcessingException e) {
            throw new TransactionsException("Failure while processing transaction", e);
        }
    }

}