package com.vmlg.bank.bank.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import com.vmlg.bank.bank.dtos.ExceptionDTO;
import com.vmlg.bank.bank.exceptions.BadRequestException;

import jakarta.persistence.EntityNotFoundException;

@RestControllerAdvice
public class ControllerExceptionsHandler {

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity threat404(EntityNotFoundException exc){
        return ResponseEntity.notFound().build();
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity threatResponseGeneralExceptions(Exception exc){
        ExceptionDTO exceptionDTO = new ExceptionDTO(exc.getMessage(), "500");
        return ResponseEntity.internalServerError().body(exceptionDTO);
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity threatBadRequestException(BadRequestException exc){
        ExceptionDTO exceptionDTO = new ExceptionDTO(exc.getMessage(), "400");
        return ResponseEntity.badRequest().body(exceptionDTO);
    }
}
