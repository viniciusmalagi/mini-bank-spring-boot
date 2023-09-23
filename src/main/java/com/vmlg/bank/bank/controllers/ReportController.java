package com.vmlg.bank.bank.controllers;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.vmlg.bank.bank.dtos.ReportDTO;
import com.vmlg.bank.bank.exceptions.UsersException;
import com.vmlg.bank.bank.repositores.transaction.TransactionReportCustom;
import com.vmlg.bank.bank.services.ReportService;

@RestController
@RequestMapping("/report")
public class ReportController {

    @Autowired
    private ReportService reportService;
    
    @GetMapping("{id}")
    public ResponseEntity getTransactionsReport(@PathVariable("id") UUID uuid) throws UsersException{
        BigDecimal balance = reportService.findUserById(uuid).getBalance();
        List<TransactionReportCustom> transactions = reportService.getAllTransactionsBySenderId(uuid);
        return ResponseEntity.ok(new ReportDTO(balance, transactions));
    } 
}
