package com.myapplication.LoanManagementSystem.dto.payments;


import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class BulkPaymentRequestDto {
    private Long loanId;
    private BigDecimal paymentAmount;
    private LocalDate paymentDate;
}
