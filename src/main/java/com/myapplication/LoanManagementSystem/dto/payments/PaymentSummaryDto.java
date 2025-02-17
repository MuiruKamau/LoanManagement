package com.myapplication.LoanManagementSystem.dto.payments;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class PaymentSummaryDto {
    private BigDecimal totalAmountRepayable;
    private BigDecimal totalAmountPaid;
    private int remainingInstallments;
    private BigDecimal remainingBalance;
}
