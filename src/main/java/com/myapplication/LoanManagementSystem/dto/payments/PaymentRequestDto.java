package com.myapplication.LoanManagementSystem.dto.payments;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class PaymentRequestDto {
    private BigDecimal paymentAmount;
    private LocalDate paymentDate;
}
