package com.vmlg.bank.bank.services.transaction;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vmlg.bank.bank.dtos.TransactionDTO;
import com.vmlg.bank.bank.exceptions.TransactionsException;

@Service
public class TransactionProducerService {
    
    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    @Value("${api.message.transaction.topic}")
    private String topic;

    public void sendMessage(TransactionDTO transactionDTO) throws TransactionsException {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            kafkaTemplate.send(topic, objectMapper.writeValueAsString(transactionDTO));
        } catch (JsonProcessingException e) {
            throw new TransactionsException("Failure while processing transaction", e);
        }
    }
}
