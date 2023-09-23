package com.vmlg.bank.bank.exceptions;

public class TransactionsException extends BadRequestException {

    public TransactionsException(String message){
        super(message);
    }

    public TransactionsException(String message, Throwable cause){
        super(message, cause);
    }
}
