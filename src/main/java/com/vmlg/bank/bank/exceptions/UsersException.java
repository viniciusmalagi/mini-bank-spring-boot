package com.vmlg.bank.bank.exceptions;

public class UsersException extends BadRequestException{

    public UsersException(String message){
        super(message);
    }

    public UsersException(String message, Throwable cause){
        super(message, cause);
    }
}
