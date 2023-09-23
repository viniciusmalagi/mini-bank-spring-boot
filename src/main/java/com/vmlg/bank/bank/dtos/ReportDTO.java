package com.vmlg.bank.bank.dtos;

import java.util.List;
import java.math.BigDecimal;
import com.vmlg.bank.bank.repositores.transaction.TransactionReportCustom;


public record ReportDTO(BigDecimal currentBalance, List<TransactionReportCustom> transactions) {
    
}
